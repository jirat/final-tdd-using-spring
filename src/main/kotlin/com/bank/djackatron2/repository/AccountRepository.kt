package com.bank.djackatron2.repository

import com.bank.djackatron2.domain.Account

interface AccountRepository {
    fun findById(srcAcctId: String): Account
    fun updateBalance(account: Account)
}
