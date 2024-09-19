package com.bank.djackatron2.service.internal

import com.bank.djackatron2.service.FeePolicy

class ZeroFeePolicy: FeePolicy {

    override fun calculateFee(transferAmount: Double): Double {
        return ZERO_AMOUNT
    }

    companion object {
        private const val ZERO_AMOUNT: Double = 0.00
    }
}