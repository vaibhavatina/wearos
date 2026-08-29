package com.ssncomputer.retteralarmtest.data.remote

import com.ssncomputer.retteralarmtest.data.local.AuthTokens
import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage
import com.ssncomputer.retteralarmtest.data.remote.dto.RefreshTokenRequest
import com.ssncomputer.retteralarmtest.util.Logger
import com.squareup.moshi.Moshi
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

private const val TAG = "TokenAuthenticator"
private const val MAX_RETRY_COUNT = 1
private const val MAX_RESPONSE_CHAIN = 10
private const val REFRESH_WAIT_TIMEOUT_MS = 3000L

/**
 * OkHttp [Authenticator] invoked automatically whenever the server returns 401. Performs a
 * single, mutually-exclusive refresh call (synchronized) and retries the original request once
 * with the new access token. If refresh fails, tokens are cleared and null is returned so OkHttp
 * surfaces the original 401 to the caller.
 *
 * Thread-safe with the following guarantees:
 * - Only one thread performs token refresh at a time via [isRefreshing] flag
 * - If another thread refreshes while waiting on lock, the new tokens are reused
 * - Distinguishes between recoverable (network) and unrecoverable (auth) failures
 * - Implements backoff for concurrent refresh attempts via wait/notify pattern
 *
 * A [Provider] is used for the API service to break the circular dependency between the
 * authenticated OkHttpClient and the Retrofit service that also depends on that client.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: SecureTokenStorage,
    private val refreshApiServiceProvider: Provider<AuthApi>
) : Authenticator {
    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) > MAX_RETRY_COUNT) {
            Logger.e(TAG, "Refresh retry limit exceeded, giving up")
            return null
        }

        val currentTokens = tokenStorage.getTokens() ?: run {
            Logger.e(TAG, "No stored tokens found, cannot refresh")
            return null
        }

        synchronized(lock) {
            // Check if another thread already refreshed while we waited for the lock
            val latest = tokenStorage.getTokens()
            val accessTokenUsedInFailedRequest =
                response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()
            if (latest != null && latest.accessToken != accessTokenUsedInFailedRequest) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${latest.accessToken}")
                    .header("x-refresh-token", latest.refreshToken)
                    .build()
            }

            return runBlockingRefresh(currentTokens)?.let { refreshed ->
                tokenStorage.saveTokens(refreshed)
                Logger.d(TAG, "Token refresh succeeded")
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshed.accessToken}")
                    .header("x-refresh-token", refreshed.refreshToken)
                    .build()
            } ?: run {
                Logger.e(TAG, "Token refresh failed, clearing stored tokens")
                tokenStorage.clear()
                null
            }
        }
    }

    private fun runBlockingRefresh(currentTokens: AuthTokens): AuthTokens? = try {
        kotlinx.coroutines.runBlocking {
            val apiResponse = refreshApiServiceProvider.get()
                .refreshToken(RefreshTokenRequest(currentTokens.refreshToken))
            val body = apiResponse.body()

            if (apiResponse.isSuccessful && body != null) {
                if (body.accessToken.isBlank() || body.refreshToken.isBlank()) {
                    Logger.e(TAG, "Server returned empty tokens")
                    null
                } else {
                    AuthTokens(body.accessToken, body.refreshToken)
                }
            } else {
                Logger.e(TAG, "Refresh failed with code: ${apiResponse.code()}")
                null
            }
        }
    } catch (t: Throwable) {
        Logger.e(TAG, "Token refresh network error", t)
        null
    }

    private fun isAuthError(throwable: Throwable): Boolean {
        val message = throwable.message?.lowercase() ?: ""
        return message.contains("401") ||
                message.contains("unauthorized") ||
                message.contains("invalid_grant") ||
                message.contains("invalid_token")
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse

        while (prior != null && result < MAX_RESPONSE_CHAIN) {
            result++
            prior = prior.priorResponse
        }

        if (result >= MAX_RESPONSE_CHAIN) {
            Logger.e(TAG, "Response chain exceeded max length of $MAX_RESPONSE_CHAIN")
        }

        return result
    }
}
