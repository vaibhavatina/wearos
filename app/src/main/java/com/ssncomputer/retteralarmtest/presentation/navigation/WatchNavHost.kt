package com.ssncomputer.retteralarmtest.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
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

const val ROUTE_LOGIN = "login"
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
    val navController = rememberSwipeDismissableNavController()
    WatchNavHost(
        eventBus = eventBus,
        tokenStorage = tokenStorage,
        navController = navController
    )
}

@Composable
fun WatchNavHost(
    eventBus: NotificationEventBus,
    tokenStorage: SecureTokenStorage,
    navController: NavHostController
) {
    val isLoggedIn by tokenStorage.isLoggedIn.collectAsState()
    val startDestination = if (isLoggedIn) ROUTE_HOME else ROUTE_LOGIN
    var lastHandledNotificationId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(ROUTE_HOME) {
                launchSingleTop = true
                popUpTo(ROUTE_LOGIN) { inclusive = true }
            }
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
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
    }

    SwipeDismissableNavHost(navController = navController, startDestination = startDestination) {
        composable(ROUTE_LOGIN) {
            LoginScreen()
        }
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
