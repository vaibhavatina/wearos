package com.ssncomputer.retteralarmtest.data.repository

import com.ssncomputer.retteralarmtest.domain.model.NotificationAction

/** Result of submitting an accept/decline action to the backend. */
sealed interface ActionResult {
    data class Success(val message: String) : ActionResult
    data class Failure(val message: String) : ActionResult
}

interface NotificationActionRepository {
    suspend fun submit(notificationId: String, action: NotificationAction): ActionResult
}
