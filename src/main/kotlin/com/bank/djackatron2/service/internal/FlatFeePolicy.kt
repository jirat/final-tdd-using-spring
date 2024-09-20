package com.bank.djackatron2.service.internal

import com.bank.djackatron2.service.FeePolicy
import org.springframework.stereotype.Service

@Service
class FlatFeePolicy(
    private val flatFee: Double = 5.00
): FeePolicy {

    override fun calculateFee(transferAmount: Double): Double {
        return flatFee
    }
}