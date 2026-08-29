package com.ssncomputer.retteralarmtest.data.remote

import com.ssncomputer.retteralarmtest.data.remote.dto.AuthTokenResponse
import com.ssncomputer.retteralarmtest.data.remote.dto.NotificationActionRequest
import com.ssncomputer.retteralarmtest.data.remote.dto.NotificationActionResponse
import com.ssncomputer.retteralarmtest.data.remote.dto.RedeemQrCodeRequest
import com.ssncomputer.retteralarmtest.data.remote.dto.VerifyOtpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WatchApiService {

    @POST("api/watch/notifications/accept")
    suspend fun acceptNotification(
        @Body request: NotificationActionRequest
    ): Response<NotificationActionResponse>

    @POST("api/watch/notifications/decline")
    suspend fun declineNotification(
        @Body request: NotificationActionRequest
    ): Response<NotificationActionResponse>

    @POST("api/auth/watch/otp/verify")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<AuthTokenResponse>

    @POST("api/auth/watch/qr/redeem")
    suspend fun redeemQrCode(
        @Body request: RedeemQrCodeRequest
    ): Response<AuthTokenResponse>
}
