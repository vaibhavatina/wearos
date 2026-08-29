package com.ssncomputer.retteralarmtest.notification

import android.content.Context
import com.cleverpush.CleverPush
import com.cleverpush.listener.NotificationOpenedListener
import com.cleverpush.listener.NotificationReceivedListener
import com.ssncomputer.retteralarmtest.BuildConfig
import com.ssncomputer.retteralarmtest.util.Logger

private const val TAG = "CleverPushSdk"

/**
 * Thin wrapper around the CleverPush Android SDK so the rest of the app depends on our own
 * abstraction rather than the vendor API directly (easier to test/mock, easier to swap SDKs).
 */
object CleverPushSdk {
    private fun sdk(context: Context): CleverPush = CleverPush.getInstance(context)

    fun initialize(context: Context) {
        sdk(context).init(
            BuildConfig.CLEVERPUSH_CHANNEL_ID,
            NotificationReceivedListener { result ->
                Logger.d(TAG, "Received CleverPush Notification: ${result.notification.title}")
            },
            NotificationOpenedListener { result ->
                Logger.d(TAG, "Opened CleverPush Notification: ${result.notification.title}")
            }
        )
        Logger.d(TAG, "CleverPush initialized for channel=${BuildConfig.CLEVERPUSH_CHANNEL_ID}")
        logSubscriptionId(context, source = "initialize")
    }

    fun logSubscriptionId(context: Context, source: String = "runtime") {
        sdk(context).getSubscriptionId { subscriptionId ->
            if (subscriptionId.isNullOrBlank()) {
                Logger.d(TAG, "CleverPush subscriptionId unavailable source=$source")
            } else {
                Logger.d(TAG, "CleverPush subscriptionId source=$source id=$subscriptionId")
            }
        }
    }

    fun registerDeviceToken(context: Context, token: String) {
        // The CleverPush SDK listens to FCM token refreshes internally in most integrations;
        // this explicit call covers setups where the app manages FCM registration itself.
        sdk(context).setSubscriptionAttribute("fcm_token", token)
        Logger.d(TAG, "FCM token forwarded to CleverPush")
    }
}
