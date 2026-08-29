package com.ssncomputer.retteralarmtest.util

import android.util.Log

/**
 * Thin logging facade so log calls are testable and central place to redact secrets.
 * Swap the [Sink] implementation to forward to Application Insights / a crash reporter.
 */
object Logger {

    interface Sink {
        fun log(tag: String, message: String, throwable: Throwable? = null)
    }

    private val androidSink = object : Sink {
        override fun log(tag: String, message: String, throwable: Throwable?) {
            if (throwable != null) Log.e(tag, message, throwable) else Log.d(tag, message)
        }
    }

    var sink: Sink = androidSink

    fun d(tag: String, message: String) = sink.log(tag, message)

    fun e(tag: String, message: String, throwable: Throwable? = null) =
        sink.log(tag, message, throwable)

    /** Redacts sensitive header values before they ever reach a log line. */
    fun redact(value: String): String =
        if (value.length <= 8) "***" else "${value.take(4)}***${value.takeLast(4)}"
}
