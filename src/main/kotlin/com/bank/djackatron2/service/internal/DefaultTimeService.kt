package com.bank.djackatron2.service.internal

import com.bank.djackatron2.service.TimeService
import java.time.LocalTime

class DefaultTimeService(
    private val openService: LocalTime,
    private val closeService: LocalTime,
): TimeService {
    override fun isServiceAvailable(testTime: LocalTime): Boolean {
        return testTime.isAfter(openService) && testTime.isBefore(closeService)
    }

}