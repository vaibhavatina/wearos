package com.ssncomputer.retteralarmtest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage
import com.ssncomputer.retteralarmtest.domain.model.NotificationPayload
import com.ssncomputer.retteralarmtest.notification.NotificationEventBus
import com.ssncomputer.retteralarmtest.presentation.login.LoginScreen
import com.ssncomputer.retteralarmtest.presentation.notificationdetail.NotificationDetailScreen
import com.ssncomputer.retteralarmtest.presentation.notificationdetail.NotificationDetailViewModel
import kotlinx.coroutines.flow.collectLatest

private const val ROUTE_HOME = "home"
private const val ROUTE_NOTIFICATION_DETAIL = "notification_detail"

/**
 * Root navigation graph. Shows the login flow until all required auth headers are stored, then
 * listens to [NotificationEventBus] so a tapped notification always routes to the details screen —
 * even if the app process was cold-started by the tap — and is idempotent per
 * [NotificationPayload.notificationId] to avoid stacking duplicate screens.
 */
@Composable
fun WatchNavHost(eventBus: NotificationEventBus, tokenStorage: SecureTokenStorage) {
    val isLoggedIn by tokenStorage.isLoggedIn.collectAsState()

    if (!isLoggedIn) {
        LoginScreen()
        return
    }

    val navController = rememberSwipeDismissableNavController()
    var lastHandledNotificationId: String? = null

    LaunchedEffect(Unit) {
        eventBus.events.collectLatest { payload ->
            if (payload.notificationId != lastHandledNotificationId) {
                lastHandledNotificationId = payload.notificationId
                navController.currentBackStackEntry?.savedStateHandle?.set("payload", payload)
                navController.navigate(ROUTE_NOTIFICATION_DETAIL) {
                    popUpTo(ROUTE_HOME)
                }
            }
        }
    }

    SwipeDismissableNavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen()
        }
        composable(ROUTE_NOTIFICATION_DETAIL) { _ ->
            val payload = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<NotificationPayload>("payload")

            val viewModel: NotificationDetailViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            LaunchedEffect(payload) {
                payload?.let(viewModel::show)
            }

            NotificationDetailScreen(
                onFinished = { navController.popBackStack(ROUTE_HOME, inclusive = false) },
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun HomeScreen() {
    androidx.wear.compose.material.Text("Waiting for notifications…")
}
