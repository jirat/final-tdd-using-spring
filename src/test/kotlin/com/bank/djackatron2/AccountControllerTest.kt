package com.bank.djackatron2

import com.bank.djackatron2.controller.AccountController
import com.bank.djackatron2.domain.Account
import com.bank.djackatron2.repository.AccountRepository
import com.bank.djackatron2.service.TransferService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class AccountControllerTest {

    private val repository: AccountRepository = mock(AccountRepository::class.java)
    private val service: TransferService = mock(TransferService::class.java)
    private val controller: AccountController = AccountController(repository, service)

    @Test
    fun testHandleById() {
        //given
        val accId = "A123"
        val account = Account(accId, 100.00)

        `when`(repository.findById(anyString())).thenReturn(account)

        //when
        val result = controller.handleById(accId)

        assertEquals(account, result)
    }

}