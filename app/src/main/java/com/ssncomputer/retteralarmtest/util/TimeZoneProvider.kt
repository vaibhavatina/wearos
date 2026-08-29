package com.ssncomputer.retteralarmtest.util

import java.util.TimeZone
import javax.inject.Inject

/** Reads the device's IANA time zone id, e.g. "Europe/Vienna". */
class TimeZoneProvider @Inject constructor() {
    fun current(): String = TimeZone.getDefault().id
}
