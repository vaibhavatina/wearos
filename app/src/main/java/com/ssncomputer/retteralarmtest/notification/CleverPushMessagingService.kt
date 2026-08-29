package com.ssncomputer.retteralarmtest.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssncomputer.retteralarmtest.R
import com.ssncomputer.retteralarmtest.domain.model.NotificationParseException
import com.ssncomputer.retteralarmtest.domain.model.NotificationPayload
import com.ssncomputer.retteralarmtest.presentation.MainActivity
import com.ssncomputer.retteralarmtest.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CleverPushMessaging"
private const val CHANNEL_ID = "notification_actions"

/**
 * CleverPush delivers watch push messages through Firebase Cloud Messaging on Wear OS.
 * This service receives the payload in foreground/background/terminated states (FCM wakes the
 * process as needed), parses it, registers the local notification, and — once the user taps it —
 * publishes it on [NotificationEventBus] so the Compose navigation graph opens the details screen.
 */
@AndroidEntryPoint
class CleverPushMessagingService : FirebaseMessagingService() {

    @Inject lateinit var eventBus: NotificationEventBus

    private val serviceScope = CoroutineScope(Dispatchers.Default)

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        Logger.d(TAG, "Push received, messageId=${message.messageId}")

        val payload = if (message.data["notificationId"].isNullOrBlank()) {
            Logger.d(TAG, "Temporary payload without notificationId, using fallback payload")
            buildFallbackPayload(message) ?: return
        } else {
            try {
                NotificationPayload.fromRawExtras(message.data)
            } catch (_: NotificationParseException) {
                Logger.d(TAG, "Invalid payload format, using fallback payload")
                buildFallbackPayload(message) ?: return
            }
        }

        showLocalNotification(payload)
    }

    override fun onNewToken(token: String) {
        // Forward the new registration token to CleverPush so the device stays registered
        // for push delivery. CleverPushSdk wraps the actual SDK call.
        CleverPushSdk.registerDeviceToken(applicationContext, token)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showLocalNotification(payload: NotificationPayload) {
        ensureChannel()

        val contentIntent = MainActivity.buildNotificationTapIntent(applicationContext, payload)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(payload.title)
            .setContentText(payload.message)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(payload.notificationId.hashCode(), notification)

        Logger.d(TAG, "Local notification posted for id=${payload.notificationId}")
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Approval requests",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    /**
     * Temporary fallback for test notifications that only include notification/body fields.
     * Keeps the app flow alive so the message can be viewed on-device.
     */
    private fun buildFallbackPayload(message: RemoteMessage): NotificationPayload? {
        val title = message.data["title"].orEmpty()
            .ifBlank { message.notification?.title.orEmpty() }
        val text = message.data["message"].orEmpty()
            .ifBlank { message.notification?.body.orEmpty() }
        if (text.isBlank()) {
            Logger.e(TAG, "Dropping notification: both data.message and notification.body are blank")
            return null
        }
        return NotificationPayload(
            notificationId = message.messageId ?: "temp-${System.currentTimeMillis()}",
            title = title,
            message = text
        )
    }

    /** Called by [MainActivity] once it has resolved the tapped [NotificationPayload]. */
    fun publish(payload: NotificationPayload) {
        serviceScope.launch { eventBus.emit(payload) }
    }
}
