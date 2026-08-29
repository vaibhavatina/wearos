package com.ssncomputer.retteralarmtest.data.remote

import com.ssncomputer.retteralarmtest.data.local.AuthTokens
import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage
import com.ssncomputer.retteralarmtest.util.TimeZoneProvider
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient
    private val tokenStorage = mockk<SecureTokenStorage>()
    private val timeZoneProvider = mockk<TimeZoneProvider> {
        every { current() } returns "Europe/Vienna"
    }

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStorage, timeZoneProvider))
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `attaches all mandatory headers when tokens are present`() {
        every { tokenStorage.getTokens() } returns AuthTokens("access-123", "refresh-456")
        server.enqueue(MockResponse().setResponseCode(200))

        val request = Request.Builder().url(server.url("/api/watch/notifications/accept")).get().build()
        client.newCall(request).execute().use { }

        val recorded = server.takeRequest()
        assertEquals("application/json", recorded.getHeader("Content-Type"))
        assertEquals("application/json", recorded.getHeader("Accept"))
        assertEquals("Bearer access-123", recorded.getHeader("Authorization"))
        assertEquals("refresh-456", recorded.getHeader("x-refresh-token"))
        assertEquals("Europe/Vienna", recorded.getHeader("time-zone"))
    }

    @Test
    fun `does not attach auth headers on the refresh endpoint`() {
        every { tokenStorage.getTokens() } returns AuthTokens("access-123", "refresh-456")
        server.enqueue(MockResponse().setResponseCode(200))

        val request = Request.Builder().url(server.url("/api/auth/refresh")).get().build()
        client.newCall(request).execute().use { }

        val recorded = server.takeRequest()
        assertNull(recorded.getHeader("Authorization"))
        assertNull(recorded.getHeader("x-refresh-token"))
    }

    @Test
    fun `omits auth headers when no tokens are stored`() {
        every { tokenStorage.getTokens() } returns null
        server.enqueue(MockResponse().setResponseCode(200))

        val request = Request.Builder().url(server.url("/api/watch/notifications/accept")).get().build()
        client.newCall(request).execute().use { }

        val recorded = server.takeRequest()
        assertNull(recorded.getHeader("Authorization"))
    }
}
