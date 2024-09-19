package com.bank.djackatron2.controller

import com.bank.djackatron2.domain.InsufficientFundsException
import com.bank.djackatron2.repository.AccountRepository
import com.bank.djackatron2.service.TransferService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account")
class AccountController(
    private val repository: AccountRepository,
    private val service: TransferService
) {

    @GetMapping("/{id}")
    fun handleById(@PathVariable("id") accId: String) =
        repository.findById(accId)

    @RequestMapping("/{srcId}/transfer/{amount}/to/{destId}")
    @Throws(InsufficientFundsException::class)
    fun handleTransfer(
        @PathVariable("srcId") srcId: String,
        @PathVariable("amount") amount: Double,
        @PathVariable("destId") destId: String
    ) = service.transfer(amount, srcId, destId)
}