package com.bank.djackatron2.domain

data class TransferReceipt(
    private val transferAmount: Double,
    private val feeAmount: Double,
    private val initialSourceAccountCopy: Account,
    private val initialDestinationAccountCopy: Account,
    private val finalSourceAccountCopy: Account,
    private val finalDestinationAccountCopy: Account
) {

    override fun toString(): String =
        """
            Transferred $transferAmount from account ${initialSourceAccountCopy.getId()} to ${initialDestinationAccountCopy.getId()}, with fee amount: $feeAmount
                initial balance for account ${initialSourceAccountCopy.getId()}: ${initialSourceAccountCopy.getBalance()}; new balance: ${finalSourceAccountCopy.getBalance()}
                initial balance for account ${initialDestinationAccountCopy.getId()}: ${initialDestinationAccountCopy.getBalance()}; new balance: ${finalDestinationAccountCopy.getBalance()}
        """.trimIndent()


}
