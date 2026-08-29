package com.ssncomputer.retteralarmtest.data.repository

import com.ssncomputer.retteralarmtest.data.local.AuthTokens
import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage
import com.ssncomputer.retteralarmtest.data.remote.WatchApiService
import com.ssncomputer.retteralarmtest.data.remote.dto.AuthTokenResponse
import com.ssncomputer.retteralarmtest.data.remote.dto.RedeemQrCodeRequest
import com.ssncomputer.retteralarmtest.data.remote.dto.VerifyOtpRequest
import com.ssncomputer.retteralarmtest.domain.model.DEVICE_TYPE_WATCH
import com.ssncomputer.retteralarmtest.util.Logger
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "AuthRepository"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong. Please try again."

/**
 * Both login paths exchange a short-lived credential for a token pair and hand it to the existing
 * [SecureTokenStorage]; no separate persistence logic exists for login.
 */
class AuthRepositoryImpl @Inject constructor(
    private val apiService: WatchApiService,
    private val tokenStorage: SecureTokenStorage
) : AuthRepository {

    override suspend fun loginWithOtp(code: String): AuthResult =
        login("OTP") { apiService.verifyOtp(VerifyOtpRequest(code = code, deviceType = DEVICE_TYPE_WATCH)) }

    override suspend fun loginWithQrCode(pairingToken: String): AuthResult =
        login("QR") {
            apiService.redeemQrCode(RedeemQrCodeRequest(pairingToken = pairingToken, deviceType = DEVICE_TYPE_WATCH))
        }

    private suspend fun login(
        method: String,
        call: suspend () -> Response<AuthTokenResponse>
    ): AuthResult {
        Logger.d(TAG, "$method login attempt started")
        return try {
            val response = call()
            val body = response.body()
            when {
                !response.isSuccessful || body == null -> {
                    Logger.e(TAG, "$method login rejected: code=${response.code()}")
                    AuthResult.Failure(errorMessage(response))
                }
                body.accessToken.isBlank() || body.refreshToken.isBlank() -> {
                    Logger.e(TAG, "$method login returned an incomplete token pair")
                    AuthResult.Failure(GENERIC_ERROR_MESSAGE)
                }
                else -> {
                    tokenStorage.saveTokens(AuthTokens(body.accessToken, body.refreshToken))
                    Logger.d(TAG, "$method login succeeded")
                    AuthResult.Success
                }
            }
        } catch (t: Throwable) {
            Logger.e(TAG, "$method login network error", t)
            AuthResult.Failure(GENERIC_ERROR_MESSAGE)
        }
    }

    private fun errorMessage(response: Response<AuthTokenResponse>): String = when (response.code()) {
        400, 401, 403 -> "Invalid or expired code. Please try again."
        else -> GENERIC_ERROR_MESSAGE
    }
}
