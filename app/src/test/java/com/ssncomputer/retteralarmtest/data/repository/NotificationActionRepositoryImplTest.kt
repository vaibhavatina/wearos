package com.ssncomputer.retteralarmtest.data.repository

import com.ssncomputer.retteralarmtest.data.remote.WatchApiService
import com.ssncomputer.retteralarmtest.data.remote.dto.NotificationActionResponse
import com.ssncomputer.retteralarmtest.domain.model.NotificationAction
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class NotificationActionRepositoryImplTest {

    private val apiService = mockk<WatchApiService>()
    private val repository = NotificationActionRepositoryImpl(apiService)

    @Test
    fun `accept success maps to ActionResult Success`() = runTest {
        coEvery { apiService.acceptNotification(any()) } returns Response.success(
            NotificationActionResponse(success = true, message = "Request Accepted Successfully")
        )

        val result = repository.submit("12345", NotificationAction.ACCEPT)

        assertTrue(result is ActionResult.Success)
        assertEquals("Request Accepted Successfully", (result as ActionResult.Success).message)
    }

    @Test
    fun `decline server error maps to ActionResult Failure with generic message`() = runTest {
        coEvery { apiService.declineNotification(any()) } returns Response.error(
            500,
            "".toResponseBody()
        )

        val result = repository.submit("12345", NotificationAction.DECLINE)

        assertTrue(result is ActionResult.Failure)
        assertEquals(
            "Something went wrong. Please try again.",
            (result as ActionResult.Failure).message
        )
    }

    @Test
    fun `network exception maps to ActionResult Failure`() = runTest {
        coEvery { apiService.acceptNotification(any()) } throws java.io.IOException("timeout")

        val result = repository.submit("12345", NotificationAction.ACCEPT)

        assertTrue(result is ActionResult.Failure)
    }
}
