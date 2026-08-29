package com.ssncomputer.retteralarmtest

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ssncomputer.retteralarmtest.data.local.AuthTokens
import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage
import com.ssncomputer.retteralarmtest.domain.model.NotificationPayload
import com.ssncomputer.retteralarmtest.notification.NotificationEventBus
import com.ssncomputer.retteralarmtest.presentation.navigation.WatchNavHost
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test: verifies that publishing a payload on [NotificationEventBus] navigates to the
 * notification details screen and renders its content, without requiring a live backend.
 */
@RunWith(AndroidJUnit4::class)
class NotificationTapNavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tappingNotificationNavigatesToDetailScreen() {
        val eventBus = NotificationEventBus()
        val tokenStorage = SecureTokenStorage(ApplicationProvider.getApplicationContext())
        tokenStorage.clear()
        tokenStorage.saveTokens(AuthTokens(accessToken = "access", refreshToken = "refresh"))

        composeRule.setContent { WatchNavHost(eventBus = eventBus, tokenStorage = tokenStorage) }

        runBlocking {
            eventBus.emit(
                NotificationPayload(
                    notificationId = "12345",
                    title = "Order Approval Required",
                    message = "Please review request",
                    data = emptyMap()
                )
            )
        }

        composeRule.onNodeWithText("Order Approval Required").assertExists()
        composeRule.onNodeWithText("Request ID: 12345").assertExists()
    }
}
