package com.bank.djackatron2.domain


data class Account(
    private val id: String,
    private var balance: Double
) {
    companion object {
        fun copy(account: Account) = Account(
            id = account.id,
            balance = account.balance
        )
    }

    fun getId(): String = id
    fun getBalance(): Double = balance

    fun setBalance(balance: Double) {
        this.balance = balance
    }

    fun debit(amount: Double) {
        assertValid(amount)
        if (amount > balance) throw InsufficientFundsException(this, amount)

        balance -= amount
    }

    fun credit(amount: Double) {
        assertValid(amount)
        balance += amount
    }

    private fun assertValid(amount: Double) = require((amount > 0.00)) { "amount must be greater than zero" }


}
