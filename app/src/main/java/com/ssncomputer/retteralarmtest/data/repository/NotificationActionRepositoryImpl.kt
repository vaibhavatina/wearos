package com.ssncomputer.retteralarmtest.data.repository

import com.ssncomputer.retteralarmtest.data.remote.WatchApiService
import com.ssncomputer.retteralarmtest.data.remote.dto.NotificationActionRequest
import com.ssncomputer.retteralarmtest.domain.model.DEVICE_TYPE_WATCH
import com.ssncomputer.retteralarmtest.domain.model.NotificationAction
import com.ssncomputer.retteralarmtest.util.Logger
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "NotificationActionRepo"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong. Please try again."

class NotificationActionRepositoryImpl @Inject constructor(
    private val apiService: WatchApiService
) : NotificationActionRepository {

    override suspend fun submit(notificationId: String, action: NotificationAction): ActionResult {
        val request = NotificationActionRequest(
            notificationId = notificationId,
            action = action.name,
            deviceType = DEVICE_TYPE_WATCH,
            timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        )

        Logger.d(TAG, "Submitting ${action.name} for notification=$notificationId")

        return try {
            val response = when (action) {
                NotificationAction.ACCEPT -> apiService.acceptNotification(request)
                NotificationAction.DECLINE -> apiService.declineNotification(request)
            }

            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Logger.d(TAG, "${action.name} succeeded for notification=$notificationId")
                ActionResult.Success(body.message)
            } else {
                Logger.e(
                    TAG,
                    "${action.name} rejected by server: code=${response.code()} body=${response.errorBody()?.string()}"
                )
                ActionResult.Failure(body?.message ?: GENERIC_ERROR_MESSAGE)
            }
        } catch (t: Throwable) {
            Logger.e(TAG, "${action.name} network error for notification=$notificationId", t)
            ActionResult.Failure(GENERIC_ERROR_MESSAGE)
        }
    }
}
