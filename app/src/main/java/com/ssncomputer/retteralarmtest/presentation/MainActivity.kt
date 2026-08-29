package com.ssncomputer.retteralarmtest.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.IntentCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage
import com.ssncomputer.retteralarmtest.domain.model.NotificationPayload
import com.ssncomputer.retteralarmtest.notification.CleverPushMessagingService
import com.ssncomputer.retteralarmtest.notification.CleverPushSdk
import com.ssncomputer.retteralarmtest.notification.NotificationEventBus
import com.ssncomputer.retteralarmtest.presentation.navigation.WatchNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var eventBus: NotificationEventBus

    @Inject lateinit var tokenStorage: SecureTokenStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        CleverPushSdk.initialize(applicationContext)

        handleNotificationIntent(intent)

        setContent {
            WatchNavHost(eventBus = eventBus, tokenStorage = tokenStorage)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        CleverPushSdk.logSubscriptionId(applicationContext, source = "reopen")
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val payload = intent?.let {
            IntentCompat.getParcelableExtra(
                it,
                EXTRA_NOTIFICATION_PAYLOAD,
                NotificationPayload::class.java
            )
        }
            ?: return
        MainScope().launch { eventBus.emit(payload) }
    }

    companion object {
        private const val EXTRA_NOTIFICATION_PAYLOAD = "extra_notification_payload"

        /** Builds the tap PendingIntent content used by [CleverPushMessagingService]. */
        fun buildNotificationTapIntent(
            context: Context,
            payload: NotificationPayload
        ): android.app.PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_NOTIFICATION_PAYLOAD, payload)
            }
            return android.app.PendingIntent.getActivity(
                context,
                payload.notificationId.hashCode(),
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
