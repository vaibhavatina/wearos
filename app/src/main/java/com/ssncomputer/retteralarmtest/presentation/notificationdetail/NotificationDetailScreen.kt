package com.ssncomputer.retteralarmtest.presentation.notificationdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

/**
 * Notification details screen focused on the received notification message and action buttons.
 * Built with [ScalingLazyColumn] so content adapts to both round and square watch screens.
 */
@Composable
fun NotificationDetailScreen(
    onFinished: () -> Unit,
    viewModel: NotificationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberScalingLazyListState()

    val payload = uiState.payload

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Notification",
                style = MaterialTheme.typography.caption2,
                textAlign = TextAlign.Center
            )
        }
        if (payload != null) {
            item {
                Text(
                    text = payload.message,
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center
                )
            }
        }

        when {
            uiState.isSubmitting -> item {
                CircularProgressIndicator(modifier = Modifier.semantics {
                    contentDescription = "Submitting action"
                })
            }

            uiState.resultMessage != null -> item {
                Text(
                    text = uiState.resultMessage.orEmpty(),
                    color = if (uiState.isError) {
                        MaterialTheme.colors.error
                    } else {
                        MaterialTheme.colors.primary
                    },
                    textAlign = TextAlign.Center
                )
            }

            else -> {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = viewModel::onAccept,
                            enabled = uiState.canSubmitAction,
                            colors = ButtonDefaults.primaryButtonColors(),
                            modifier = Modifier.semantics { contentDescription = "Accept request" }
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                        Button(
                            onClick = viewModel::onDecline,
                            enabled = uiState.canSubmitAction,
                            colors = ButtonDefaults.secondaryButtonColors(),
                            modifier = Modifier.semantics { contentDescription = "Decline request" }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                }
            }
        }

        if (uiState.isError) {
            item {
                Button(onClick = viewModel::dismissResult) {
                    Text("Try again")
                }
            }
        }
    }

    LaunchedEffectFinishOnSuccess(uiState, onFinished)
}

@Composable
private fun LaunchedEffectFinishOnSuccess(
    uiState: NotificationDetailUiState,
    onFinished: () -> Unit
) {
    androidx.compose.runtime.LaunchedEffect(uiState.resultMessage, uiState.isError) {
        if (uiState.resultMessage != null && !uiState.isError) {
            kotlinx.coroutines.delay(1500)
            onFinished()
        }
    }
}
