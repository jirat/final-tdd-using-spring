package com.bank.djackatron2.service

import com.bank.djackatron2.domain.TransferReceipt
import org.springframework.stereotype.Service

@Service
interface TransferService {

    fun transfer(amount: Double, srcAcctId: String, dstAcctId: String): TransferReceipt

    fun setMinimumTransferAmount(minimumTransferAmount: Double)

    fun setTimeService(timeService: TimeService)
}
