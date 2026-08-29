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

/**
 * OkHttp [Authenticator] invoked automatically whenever the server returns 401. Performs a
 * single, mutually-exclusive refresh call (synchronized) and retries the original request once
 * with the new access token. If refresh fails, tokens are cleared and null is returned so OkHttp
 * surfaces the original 401 to the caller.
 *
 * A [Provider] is used for the API service to break the circular dependency between the
 * authenticated OkHttpClient and the Retrofit service that also depends on that client.
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: SecureTokenStorage,
    private val refreshApiServiceProvider: Provider<WatchApiService>,
    private val moshi: Moshi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) > MAX_RETRY_COUNT) {
            Logger.e(TAG, "Refresh retry limit exceeded, giving up")
            return null
        }

        val currentTokens = tokenStorage.getTokens() ?: return null

        synchronized(this) {
            // Another thread may have already refreshed while we were waiting on the lock.
            val latest = tokenStorage.getTokens()
            val accessTokenUsedInFailedRequest =
                response.request.header("Authorization")?.removePrefix("Bearer ")
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
                AuthTokens(body.accessToken, body.refreshToken)
            } else {
                null
            }
        }
    } catch (t: Throwable) {
        Logger.e(TAG, "Token refresh network error", t)
        null
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
