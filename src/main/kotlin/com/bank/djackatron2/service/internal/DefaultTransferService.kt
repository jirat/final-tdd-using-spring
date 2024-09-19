package com.bank.djackatron2.service.internal

import com.bank.djackatron2.domain.Account
import com.bank.djackatron2.domain.InsufficientFundsException
import com.bank.djackatron2.domain.TransferReceipt
import com.bank.djackatron2.repository.AccountRepository
import com.bank.djackatron2.service.FeePolicy
import com.bank.djackatron2.service.OutOfServiceException
import com.bank.djackatron2.service.TimeService
import com.bank.djackatron2.service.TransferService
import java.time.LocalTime

class DefaultTransferService(
    private val accountRepository: AccountRepository,
    private val feePolicy: FeePolicy,
): TransferService {

    private var minimumTransferAmount = 1.00
    private var timeService: TimeService? = null

    override fun transfer(amount: Double, srcAcctId: String, dstAcctId: String): TransferReceipt {
        if (amount < minimumTransferAmount) throw IllegalArgumentException("transfer amount must be at least $minimumTransferAmount")

        if (timeService != null && !timeService!!.isServiceAvailable(LocalTime.now())) throw OutOfServiceException()

        val srcAcct: Account = accountRepository.findById(srcAcctId)
        val dstAcct: Account = accountRepository.findById(dstAcctId)

        val receipt = TransferReceipt(
            initialSourceAccountCopy = srcAcct,
            initialDestinationAccountCopy = dstAcct,
        )

        val fee = feePolicy.calculateFee(amount)
        if (fee > 0) {
            try {
                srcAcct.debit(fee)
            } catch (e: InsufficientFundsException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }

        receipt.setTransferAmount(amount)
        receipt.setFeeAmount(fee)

        try {
            srcAcct.debit(amount)
        } catch (e: InsufficientFundsException) {
            throw  InsufficientFundsException(srcAcct, amount)
        }

        dstAcct.credit(amount)

        accountRepository.updateBalance(srcAcct)
        accountRepository.updateBalance(dstAcct)

        receipt.setFinalSourceAccount(srcAcct)
        receipt.setFinalDestinationAccount(dstAcct)

        return receipt
    }

    override fun setMinimumTransferAmount(minimumTransferAmount: Double) {
        this.minimumTransferAmount = minimumTransferAmount
    }

    override fun setTimeService(timeService: TimeService) {
        TODO("Not yet implemented")
    }

}