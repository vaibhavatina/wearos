package com.ssncomputer.retteralarmtest.data.remote

import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage
import com.ssncomputer.retteralarmtest.util.TimeZoneProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Attaches the mandatory header set to every outgoing request:
 * Content-Type, Accept, Authorization, x-refresh-token, time-zone.
 *
 * The refresh endpoint itself is excluded from Authorization/x-refresh-token injection since it
 * carries the refresh token in its body instead (see [WatchApiService.refreshToken]).
 */
class AuthInterceptor @Inject constructor(
    private val tokenStorage: SecureTokenStorage,
    private val timeZoneProvider: TimeZoneProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("time-zone", timeZoneProvider.current())

        val isRefreshCall = original.url.encodedPath.endsWith("/api/auth/refresh")
        if (!isRefreshCall) {
            tokenStorage.getTokens()?.let { tokens ->
                builder
                    .header("Authorization", "Bearer ${tokens.accessToken}")
                    .header("x-refresh-token", tokens.refreshToken)
            }
        }

        return chain.proceed(builder.build())
    }
}
