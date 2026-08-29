package com.ssncomputer.retteralarmtest.data.repository

/** Result of an OTP or QR login attempt. */
sealed interface AuthResult {
    data object Success : AuthResult
    data class Failure(val message: String) : AuthResult
}

interface AuthRepository {
    suspend fun loginWithOtp(code: String): AuthResult
    suspend fun loginWithQrCode(pairingToken: String): AuthResult
}
