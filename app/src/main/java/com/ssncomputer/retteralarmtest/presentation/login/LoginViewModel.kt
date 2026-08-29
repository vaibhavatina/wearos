package com.ssncomputer.retteralarmtest.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssncomputer.retteralarmtest.data.repository.AuthRepository
import com.ssncomputer.retteralarmtest.data.repository.AuthResult
import com.ssncomputer.retteralarmtest.util.QrCodePayloadParser
import com.ssncomputer.retteralarmtest.util.QrScanException
import com.ssncomputer.retteralarmtest.util.QrCodeScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val OTP_LENGTH = 6

data class LoginUiState(
    val otpCode: String = "",
    val pairingCode: String = "",
    val isScannerAvailable: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val canSubmitOtp: Boolean
        get() = !isSubmitting && otpCode.length == OTP_LENGTH && otpCode.all(Char::isDigit)

    val canSubmitPairingCode: Boolean
        get() = !isSubmitting && (isScannerAvailable || pairingCode.isNotBlank())

    val message: String?
        get() = errorMessage ?: successMessage
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val qrCodeScanner: QrCodeScanner
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(isScannerAvailable = qrCodeScanner.isAvailable()))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onOtpChanged(value: String) {
        _uiState.value = _uiState.value.copy(otpCode = value.filter(Char::isDigit).take(OTP_LENGTH))
    }

    fun onPairingCodeChanged(value: String) {
        _uiState.value = _uiState.value.copy(pairingCode = value)
    }

    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun submitOtp() {
        if (!_uiState.value.canSubmitOtp) return
        startLogin { repository.loginWithOtp(_uiState.value.otpCode) }
    }

    /** Uses the device scanner when available, otherwise the manually entered pairing code. */
    fun submitQrCode() {
        if (!_uiState.value.canSubmitPairingCode) return
        startLogin {
            try {
                val scanned = if (_uiState.value.isScannerAvailable) {
                    qrCodeScanner.scan()
                } else {
                    _uiState.value.pairingCode
                }
                repository.loginWithQrCode(QrCodePayloadParser.pairingToken(scanned))
            } catch (e: QrScanException) {
                AuthResult.Failure(e.reason.message)
            }
        }
    }

    private fun startLogin(operation: suspend () -> AuthResult) {
        _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null, successMessage = null)
        viewModelScope.launch {
            when (val result = operation()) {
                // The navigation graph observes the stored tokens and swaps to the notification flow.
                is AuthResult.Success -> _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    otpCode = "",
                    pairingCode = "",
                    successMessage = "Signed in successfully"
                )
                is AuthResult.Failure -> _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = result.message
                )
            }
        }
    }
}
