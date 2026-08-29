package com.ssncomputer.retteralarmtest.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NotificationPayloadTest {

    @Test
    fun `parses full payload including metadata`() {
        val extras = mapOf(
            "notificationId" to "12345",
            "title" to "Order Approval Required",
            "message" to "Please review request",
            "requestType" to "ORDER"
        )

        val payload = NotificationPayload.fromRawExtras(extras)

        assertEquals("12345", payload.notificationId)
        assertEquals("Order Approval Required", payload.title)
        assertEquals("Please review request", payload.message)
        assertEquals(mapOf("requestType" to "ORDER"), payload.data)
    }

    @Test
    fun `missing notificationId throws parse exception`() {
        val extras = mapOf("title" to "t", "message" to "m")

        assertThrows(NotificationParseException::class.java) {
            NotificationPayload.fromRawExtras(extras)
        }
    }

    @Test
    fun `blank notificationId throws parse exception`() {
        val extras = mapOf("notificationId" to "  ", "title" to "t")

        assertThrows(NotificationParseException::class.java) {
            NotificationPayload.fromRawExtras(extras)
        }
    }

    @Test
    fun `missing data defaults to empty map`() {
        val extras = mapOf("notificationId" to "1", "title" to "t", "message" to "m")

        val payload = NotificationPayload.fromRawExtras(extras)

        assertEquals(emptyMap<String, String>(), payload.data)
    }
}
