package com.bank.djackatron2.service.internal

import com.bank.djackatron2.service.FeePolicy

class VariableFeePolicy(
    private val maxFreeFee: Double,
    private val maxPercentFee: Double,
    private val percentage: Double,
    private val flatRate: Double
): FeePolicy {

    override fun calculateFee(transferAmount: Double): Double {
        if (transferAmount <= maxFreeFee) return 0.00
        if (transferAmount <= maxPercentFee) return (transferAmount * percentage / 100)

        return flatRate
    }
}