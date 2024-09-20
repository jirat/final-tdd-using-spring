package com.bank.djackatron2.repository

import com.bank.djackatron2.domain.Account
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository {
    fun findById(srcAcctId: String): Account
    fun updateBalance(account: Account)
}
