package com.bank.djackatron2.domain

class InsufficientFundsException(
    private val targetAccount: Account,
    private val attemptedAmount: Double
): Exception() {

    private fun getTargetAccountId(): String = targetAccount.getId()

    private fun getTargetAccountBalance(): Double = targetAccount.getBalance()

    private fun getAttemptedAmount(): Double = attemptedAmount

    private fun getOverage() = attemptedAmount - targetAccount.getBalance()

    override fun toString(): String =
        """
            Failed to transfer ${getAttemptedAmount()} from account ${getTargetAccountId()} Reason: insufficient funds
                current balance for target account is ${getTargetAccountBalance()}
                transfer overage is ${getOverage()}
        """.trimIndent()
}