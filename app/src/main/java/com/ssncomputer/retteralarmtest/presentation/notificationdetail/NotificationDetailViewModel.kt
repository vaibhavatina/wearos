package com.ssncomputer.retteralarmtest.presentation.notificationdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssncomputer.retteralarmtest.data.repository.ActionResult
import com.ssncomputer.retteralarmtest.data.repository.NotificationActionRepository
import com.ssncomputer.retteralarmtest.domain.model.NotificationAction
import com.ssncomputer.retteralarmtest.domain.model.NotificationPayload
import com.ssncomputer.retteralarmtest.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NotificationDetailVM"

@HiltViewModel
class NotificationDetailViewModel @Inject constructor(
    private val repository: NotificationActionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationDetailUiState())
    val uiState: StateFlow<NotificationDetailUiState> = _uiState.asStateFlow()

    fun show(payload: NotificationPayload) {
        Logger.d(TAG, "Displaying details for notification=${payload.notificationId}")
        _uiState.value = NotificationDetailUiState(payload = payload)
    }

    fun onAccept() = submit(NotificationAction.ACCEPT)

    fun onDecline() = submit(NotificationAction.DECLINE)

    private fun submit(action: NotificationAction) {
        val payload = _uiState.value.payload ?: return
        if (!_uiState.value.canSubmitAction) return

        Logger.d(TAG, "${action.name} tapped for notification=${payload.notificationId}")
        _uiState.value = _uiState.value.copy(isSubmitting = true)

        viewModelScope.launch {
            when (val result = repository.submit(payload.notificationId, action)) {
                is ActionResult.Success -> _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    resultMessage = successMessage(action),
                    isError = false
                )
                is ActionResult.Failure -> _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    resultMessage = result.message,
                    isError = true
                )
            }
        }
    }

    /** Allows the user to retry after a failure without losing the displayed notification. */
    fun dismissResult() {
        _uiState.value = _uiState.value.copy(resultMessage = null, isError = false)
    }

    private fun successMessage(action: NotificationAction) = when (action) {
        NotificationAction.ACCEPT -> "Request Accepted Successfully"
        NotificationAction.DECLINE -> "Request Declined Successfully"
    }
}
