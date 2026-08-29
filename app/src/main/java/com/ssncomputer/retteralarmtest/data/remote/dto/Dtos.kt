package com.ssncomputer.retteralarmtest.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationActionRequest(
    val notificationId: String,
    val action: String,
    val deviceType: String,
    val timestamp: String
)

@JsonClass(generateAdapter = true)
data class NotificationActionResponse(
    val success: Boolean,
    val message: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(
    val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    @Json(name = "expiresIn") val expiresInSeconds: Long
)

/** Every endpoint that mints a token pair (refresh, OTP login, QR login) shares this shape. */
typealias AuthTokenResponse = RefreshTokenResponse

@JsonClass(generateAdapter = true)
data class VerifyOtpRequest(
    val code: String,
    val deviceType: String
)

@JsonClass(generateAdapter = true)
data class RedeemQrCodeRequest(
    val pairingToken: String,
    val deviceType: String
)
