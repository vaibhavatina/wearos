package com.ssncomputer.retteralarmtest.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Failure reasons surfaced to the user during QR login. */
enum class QrScanError(val message: String) {
    CAMERA_UNAVAILABLE("This watch cannot scan QR codes. Enter the pairing code shown under the QR code in the mobile app."),
    INVALID_CODE("This QR code is not a valid login code.")
}

class QrScanException(val reason: QrScanError) : Exception(reason.message)

interface QrCodeScanner {
    /** Drives whether the UI offers scanning or the manual pairing-code fallback. */
    fun isAvailable(): Boolean

    /** Returns the raw value encoded in the scanned QR code. */
    suspend fun scan(): String
}

/**
 * Most Wear OS watches ship without a camera, so scanning is only offered when the hardware
 * feature is actually present. Wiring an ML Kit/CameraX scanner in here is the only change needed
 * if a camera-equipped device is targeted; the rest of the login flow stays untouched.
 */
@Singleton
class DeviceQrCodeScanner @Inject constructor(
    @ApplicationContext private val context: Context
) : QrCodeScanner {

    override fun isAvailable(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    override suspend fun scan(): String = throw QrScanException(QrScanError.CAMERA_UNAVAILABLE)
}

object QrCodePayloadParser {

    /**
     * Accepts either the raw pairing token or a deep link such as
     * `retteralarm://watch-login?token=<pairingToken>`.
     */
    fun pairingToken(scannedValue: String): String {
        val trimmed = scannedValue.trim()
        if (trimmed.isEmpty()) throw QrScanException(QrScanError.INVALID_CODE)

        val uri = runCatching { Uri.parse(trimmed) }.getOrNull()
        if (uri?.scheme != null) {
            val token = runCatching { uri.getQueryParameter("token") }.getOrNull()
            if (!token.isNullOrBlank()) return token
        }

        if (trimmed.any { it.isWhitespace() }) throw QrScanException(QrScanError.INVALID_CODE)
        return trimmed
    }
}
