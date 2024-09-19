package com.bank.djackatron2.service

import java.time.LocalTime

interface TimeService {
    fun isServiceAvailable(testTime: LocalTime): Boolean
}
