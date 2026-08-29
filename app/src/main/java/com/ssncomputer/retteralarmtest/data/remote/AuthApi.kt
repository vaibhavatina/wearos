package com.ssncomputer.retteralarmtest.data.remote

import com.ssncomputer.retteralarmtest.data.remote.dto.AuthTokenResponse
import com.ssncomputer.retteralarmtest.data.remote.dto.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<AuthTokenResponse>
}
