package com.ssncomputer.retteralarmtest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Action the user chose to submit for a given notification. */
enum class NotificationAction {
    ACCEPT,
    DECLINE
}

/** Device type reported to the backend for every action submission. */
const val DEVICE_TYPE_WATCH = "WATCH"

/**
 * Parsed representation of the CleverPush payload:
 * { "notificationId": "", "title": "", "message": "", "data": {} }
 */
@Parcelize
data class NotificationPayload(
    val notificationId: String,
    val title: String,
    val message: String,
    val data: Map<String, String> = emptyMap()
) : Parcelable {
    companion object {
        /**
         * Builds a [NotificationPayload] from raw CleverPush key/value extras.
         * Throws [NotificationParseException] when required fields are missing/blank so the
         * caller can log and drop the notification instead of crashing the receiver.
         */
        fun fromRawExtras(extras: Map<String, String?>): NotificationPayload {
            val notificationId = extras["notificationId"]?.takeIf { it.isNotBlank() }
                ?: throw NotificationParseException("Missing notificationId")
            val title = extras["title"].orEmpty()
            val message = extras["message"].orEmpty()
            val data = extras.filterKeys { it !in RESERVED_KEYS }
                .mapNotNull { (k, v) -> v?.let { k to it } }
                .toMap()
            return NotificationPayload(notificationId, title, message, data)
        }

        private val RESERVED_KEYS = setOf("notificationId", "title", "message")
    }
}

class NotificationParseException(message: String) : Exception(message)
