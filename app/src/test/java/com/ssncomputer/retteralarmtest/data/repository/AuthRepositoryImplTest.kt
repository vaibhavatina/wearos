package com.ssncomputer.retteralarmtest.data.repository

import com.ssncomputer.retteralarmtest.data.local.AuthTokens
import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage
import com.ssncomputer.retteralarmtest.data.remote.WatchApiService
import com.ssncomputer.retteralarmtest.data.remote.dto.AuthTokenResponse
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class AuthRepositoryImplTest {

    private val apiService: WatchApiService = mockk()
    private val tokenStorage: SecureTokenStorage = mockk(relaxed = true)
    private val sut = AuthRepositoryImpl(apiService, tokenStorage)

    @Test
    fun `otp login persists returned tokens`() = runTest {
        coEvery { apiService.verifyOtp(any()) } returns
            Response.success(AuthTokenResponse("access", "refresh", 3600))

        val result = sut.loginWithOtp("123456")

        assertEquals(AuthResult.Success, result)
        verify { tokenStorage.saveTokens(AuthTokens("access", "refresh")) }
    }

    @Test
    fun `qr login failure does not persist tokens`() = runTest {
        coEvery { apiService.redeemQrCode(any()) } returns
            Response.error(401, "".toResponseBody("application/json".toMediaType()))

        val result = sut.loginWithQrCode("token")

        assertEquals(AuthResult.Failure("Invalid or expired code. Please try again."), result)
        verify(exactly = 0) { tokenStorage.saveTokens(any()) }
    }

    @Test
    fun `incomplete token pair is rejected`() = runTest {
        coEvery { apiService.verifyOtp(any()) } returns
            Response.success(AuthTokenResponse("access", "", 3600))

        val result = sut.loginWithOtp("123456")

        assertEquals(AuthResult.Failure("Something went wrong. Please try again."), result)
        verify(exactly = 0) { tokenStorage.saveTokens(any()) }
    }
}
