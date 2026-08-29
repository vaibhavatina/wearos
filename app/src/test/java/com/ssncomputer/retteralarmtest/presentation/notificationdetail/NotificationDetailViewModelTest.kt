package com.ssncomputer.retteralarmtest.presentation.notificationdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.ssncomputer.retteralarmtest.data.repository.ActionResult
import com.ssncomputer.retteralarmtest.data.repository.NotificationActionRepository
import com.ssncomputer.retteralarmtest.domain.model.NotificationAction
import com.ssncomputer.retteralarmtest.domain.model.NotificationPayload
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationDetailViewModelTest {

    private val repository = mockk<NotificationActionRepository>()
    private lateinit var viewModel: NotificationDetailViewModel
    private val payload = NotificationPayload("12345", "Order Approval Required", "Please review", emptyMap())

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = NotificationDetailViewModel(repository, SavedStateHandle())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `show populates payload in ui state`() = runTest {
        viewModel.show(payload)

        assertEquals(payload, viewModel.uiState.value.payload)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `accept success updates state with success message`() = runTest {
        coEvery { repository.submit("12345", NotificationAction.ACCEPT) } returns
            ActionResult.Success("Request Accepted Successfully")

        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.show(payload)
            awaitItem() // payload set

            viewModel.onAccept()
            awaitItem() // isSubmitting = true

            advanceUntilIdle()
            val success = awaitItem()
            assertEquals("Request Accepted Successfully", success.resultMessage)
            assertFalse(success.isError)
        }
    }

    @Test
    fun `decline failure updates state with error message`() = runTest {
        coEvery { repository.submit("12345", NotificationAction.DECLINE) } returns
            ActionResult.Failure("Something went wrong. Please try again.")

        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.show(payload)
            awaitItem()

            viewModel.onDecline()
            awaitItem() // submitting

            advanceUntilIdle()
            val failure = awaitItem()
            assertEquals("Something went wrong. Please try again.", failure.resultMessage)
            assert(failure.isError)
        }
    }

    @Test
    fun `double tap while submitting does not trigger a second request`() = runTest {
        coEvery { repository.submit(any(), any()) } returns ActionResult.Success("ok")

        viewModel.show(payload)
        viewModel.onAccept()
        viewModel.onAccept() // second tap should be ignored while isSubmitting=true
        advanceUntilIdle()

        io.mockk.coVerify(exactly = 1) { repository.submit(any(), any()) }
    }
}
