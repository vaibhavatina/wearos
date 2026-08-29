package com.ssncomputer.retteralarmtest.presentation.notificationdetail

import com.ssncomputer.retteralarmtest.domain.model.NotificationPayload

/** UI state consumed by [NotificationDetailScreen]. */
data class NotificationDetailUiState(
    val payload: NotificationPayload? = null,
    val isSubmitting: Boolean = false,
    val resultMessage: String? = null,
    val isError: Boolean = false
) {
    val canSubmitAction: Boolean get() = payload != null && !isSubmitting && resultMessage == null
}
