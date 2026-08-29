package com.ssncomputer.retteralarmtest.notification

import com.ssncomputer.retteralarmtest.domain.model.NotificationPayload
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-process event bus that carries a tapped notification's payload from the point it is
 * received (messaging service / deep link) to the navigation layer that decides which screen to
 * show. Using a hot [SharedFlow] with replay=1 means a payload delivered before the UI subscribes
 * (cold start) is not lost.
 */
@Singleton
class NotificationEventBus @Inject constructor() {

    private val _events = MutableSharedFlow<NotificationPayload>(replay = 1, extraBufferCapacity = 4)
    val events: SharedFlow<NotificationPayload> = _events

    suspend fun emit(payload: NotificationPayload) {
        _events.emit(payload)
    }
}
