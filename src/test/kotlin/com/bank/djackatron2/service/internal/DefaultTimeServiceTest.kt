package com.bank.djackatron2.service.internal

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalTime

class DefaultTimeServiceTest {

    @Test
    fun testIsServiceAvailable() {
        val openService = LocalTime.of(6, 0)
        val closeService = LocalTime.of(22, 0)
        val testTime = LocalTime.of(9, 0)

        val timeService = DefaultTimeService(openService, closeService)

        val result = timeService.isServiceAvailable(testTime)

        assertTrue(result)
    }
}