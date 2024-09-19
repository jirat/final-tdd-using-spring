package com.bank.djackatron2.service

interface FeePolicy {
    fun calculateFee(transferAmount: Double): Double
}
