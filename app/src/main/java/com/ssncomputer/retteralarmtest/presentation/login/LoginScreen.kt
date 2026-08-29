package com.ssncomputer.retteralarmtest.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

private enum class LoginMode { CHOICE, OTP, QR }

/**
 * Login entry point shown whenever the required auth headers are missing. Offers OTP and QR based
 * sign-in; both paths end in the shared token storage used by the notification flow.
 */
@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var mode by remember { mutableStateOf(LoginMode.CHOICE) }

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberScalingLazyListState(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Sign in",
                style = MaterialTheme.typography.title3,
                textAlign = TextAlign.Center
            )
        }

        when (mode) {
            LoginMode.CHOICE -> {
                item {
                    Chip(
                        onClick = { mode = LoginMode.OTP },
                        label = { Text("Login with OTP") },
                        colors = ChipDefaults.primaryChipColors(),
                        modifier = Modifier.semantics { contentDescription = "Login with OTP" }
                    )
                }
                item {
                    Chip(
                        onClick = { mode = LoginMode.QR },
                        label = { Text("Login with QR Code") },
                        colors = ChipDefaults.secondaryChipColors(),
                        modifier = Modifier.semantics { contentDescription = "Login with QR code" }
                    )
                }
            }

            LoginMode.OTP -> {
                item { Hint("Enter the 6-digit code sent to you.") }
                item {
                    CodeField(
                        value = uiState.otpCode,
                        placeholder = "000000",
                        keyboardType = KeyboardType.NumberPassword,
                        onValueChange = viewModel::onOtpChanged,
                        description = "OTP input"
                    )
                }
                item {
                    SubmitButton(
                        text = "Verify",
                        enabled = uiState.canSubmitOtp,
                        isSubmitting = uiState.isSubmitting,
                        onClick = viewModel::submitOtp,
                        description = "Verify OTP"
                    )
                }
            }

            LoginMode.QR -> {
                if (uiState.isScannerAvailable) {
                    item { Hint("Point your watch at the QR code shown in the mobile app.") }
                } else {
                    // Wear devices usually have no camera, so the pairing code printed with the
                    // QR code is entered instead — same backend contract.
                    item { Hint("Enter the pairing code shown under the QR code in the mobile app.") }
                    item {
                        CodeField(
                            value = uiState.pairingCode,
                            placeholder = "Pairing code",
                            keyboardType = KeyboardType.Text,
                            onValueChange = viewModel::onPairingCodeChanged,
                            description = "Pairing code input"
                        )
                    }
                }
                item {
                    SubmitButton(
                        text = if (uiState.isScannerAvailable) "Scan QR Code" else "Continue",
                        enabled = uiState.canSubmitPairingCode,
                        isSubmitting = uiState.isSubmitting,
                        onClick = viewModel::submitQrCode,
                        description = "Submit QR login"
                    )
                }
            }
        }

        uiState.message?.let { message ->
            item {
                Text(
                    text = message,
                    color = if (uiState.errorMessage != null) {
                        MaterialTheme.colors.error
                    } else {
                        MaterialTheme.colors.primary
                    },
                    style = MaterialTheme.typography.caption1,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Chip(
                    onClick = viewModel::dismissMessage,
                    label = { Text("OK") },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
        }

        if (mode != LoginMode.CHOICE) {
            item {
                Chip(
                    onClick = {
                        viewModel.dismissMessage()
                        mode = LoginMode.CHOICE
                    },
                    label = { Text("Back") },
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
        }
    }
}

@Composable
private fun Hint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.caption2,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun CodeField(
    value: String,
    placeholder: String,
    keyboardType: KeyboardType,
    onValueChange: (String) -> Unit,
    description: String
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = MaterialTheme.typography.body1.copy(
            color = MaterialTheme.colors.onSurface,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .semantics { contentDescription = description },
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            innerTextField()
        }
    )
}

@Composable
private fun SubmitButton(
    text: String,
    enabled: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
    description: String
) {
    if (isSubmitting) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = "Signing in" }
        )
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.semantics { contentDescription = description }
        ) {
            Text(text)
        }
    }
}
