package com.bank.djackatron2.service.internal

import com.bank.djackatron2.domain.Account
import com.bank.djackatron2.domain.InsufficientFundsException
import com.bank.djackatron2.domain.TransferReceipt
import com.bank.djackatron2.repository.AccountRepository
import com.bank.djackatron2.repository.internal.SimpleAccountRepository
import com.bank.djackatron2.repository.internal.SimpleAccountRepository.Companion.A123_ID
import com.bank.djackatron2.repository.internal.SimpleAccountRepository.Companion.A123_INITIAL_BAL
import com.bank.djackatron2.repository.internal.SimpleAccountRepository.Companion.C456_ID
import com.bank.djackatron2.repository.internal.SimpleAccountRepository.Companion.C456_INITIAL_BAL
import com.bank.djackatron2.repository.internal.SimpleAccountRepository.Companion.Z999_ID
import com.bank.djackatron2.service.FeePolicy
import com.bank.djackatron2.service.OutOfServiceException
import com.bank.djackatron2.service.TimeService
import com.bank.djackatron2.service.TransferService
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import java.time.LocalTime
import javax.security.auth.login.AccountNotFoundException
import kotlin.test.fail

@TestInstance(PER_CLASS)
class DefaultTransferServiceTest {

    private lateinit var accountRepository: AccountRepository
    private lateinit var transferService: TransferService

    @BeforeAll
    fun setUp() {
        accountRepository = SimpleAccountRepository()
        val feePolicy = ZeroFeePolicy()

        transferService = DefaultTransferService(accountRepository, feePolicy)

        assertThat(accountRepository.findById(A123_ID).getBalance(), CoreMatchers.equalTo(A123_INITIAL_BAL))
        assertThat(accountRepository.findById(C456_ID).getBalance(), CoreMatchers.equalTo(C456_INITIAL_BAL))
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testTransfer() {
        val transferAmount = 100.00

        //when
        val receipt = transferService.transfer(transferAmount, A123_ID, C456_ID)

        //then
        assertThat(receipt.getTransferAmount(), CoreMatchers.equalTo(transferAmount))
        assertThat(
            receipt.getFinalSourceAccount().getBalance(),
            CoreMatchers.equalTo(A123_INITIAL_BAL - transferAmount)
        )
        assertThat(
            receipt.getFinalDestinationAccount().getBalance(),
            CoreMatchers.equalTo(C456_INITIAL_BAL + transferAmount)
        )

        assertThat(
            accountRepository.findById(A123_ID).getBalance(),
            CoreMatchers.equalTo(A123_INITIAL_BAL - transferAmount)
        )
        assertThat(
            accountRepository.findById(C456_ID).getBalance(),
            CoreMatchers.equalTo(C456_INITIAL_BAL + transferAmount)
        )
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testTransferUsingDynamicStub() {
        //given
        val transferAmount = 100.00
        val srcAccId = "A123"
        val srcAcc = Account(srcAccId, 100.00)
        val desAccId = "C456"
        val desAcc = Account(desAccId, 0.00)

        val mockAccReop: AccountRepository = mock(AccountRepository::class.java)
        `when`(mockAccReop.findById(srcAccId)).thenReturn(srcAcc)
        `when`(mockAccReop.findById(desAccId)).thenReturn(desAcc)

        val mockFeePolicy = mock(FeePolicy::class.java)
        `when`(mockFeePolicy.calculateFee(anyDouble())).thenReturn(0.00)

        val transferService: TransferService = DefaultTransferService(mockAccReop, mockFeePolicy)


        //when
        val receipt = transferService.transfer(transferAmount, srcAccId, desAccId)

        //then
        assertThat(receipt.getTransferAmount(), CoreMatchers.equalTo(transferAmount))
        assertThat(receipt.getFinalSourceAccount().getBalance(), CoreMatchers.equalTo(0.00))
        assertThat(receipt.getFinalDestinationAccount().getBalance(), CoreMatchers.equalTo(100.00))
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testTransferWithCheckingTimeService() {
        //given
        val transferAmount = 100.00
        val mockTimeService = mock(TimeService::class.java)
        `when`(mockTimeService.isServiceAvailable(any<LocalTime>())).thenReturn(true)
        transferService.setTimeService(mockTimeService)

        //when
        val receipt: TransferReceipt = transferService.transfer(transferAmount, A123_ID, C456_ID)

        //then
        assertThat(receipt.getTransferAmount(), CoreMatchers.equalTo(transferAmount))
        assertThat(
            receipt.getFinalSourceAccount().getBalance(),
            CoreMatchers.equalTo(A123_INITIAL_BAL - transferAmount)
        )
        assertThat(
            receipt.getFinalDestinationAccount().getBalance(),
            CoreMatchers.equalTo(C456_INITIAL_BAL + transferAmount)
        )

        assertThat(
            accountRepository.findById(A123_ID).getBalance(),
            CoreMatchers.equalTo(A123_INITIAL_BAL - transferAmount)
        )
        assertThat(
            accountRepository.findById(C456_ID).getBalance(),
            CoreMatchers.equalTo(C456_INITIAL_BAL + transferAmount)
        )
        //verify behavior
        verify(mockTimeService).isServiceAvailable(any<LocalTime>())
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testTransferWithCheckingOutofTimeService() {
        //given
        val transferAmount = 100.00
        val mockTimeService = mock(TimeService::class.java)
        `when`(mockTimeService.isServiceAvailable(any<LocalTime>())).thenReturn(false)
        transferService.setTimeService(mockTimeService)

        //when
        try {
            val receipt = transferService.transfer(transferAmount, A123_ID, C456_ID)
            fail()
        } catch (e: OutOfServiceException) {
            //then
            //verify behavior
            verify(mockTimeService).isServiceAvailable(any<LocalTime>())
        }
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testInsufficientFunds() {
        val overage = 9.00
        val transferAmount = A123_INITIAL_BAL + overage

        assertThrows<InsufficientFundsException> { transferService.transfer(transferAmount, A123_ID, C456_ID)  }
        //fail("expected InsufficientFundsException");
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testNonExistentSourceAccount() {
        try {
            transferService.transfer(1.00, Z999_ID, C456_ID)
            fail("expected AccountNotFoundException")
        } catch (ex: AccountNotFoundException) {
        }

        assertThat(accountRepository.findById(C456_ID).getBalance(), CoreMatchers.equalTo(C456_INITIAL_BAL))
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testNonExistentDestinationAccount() {
        try {
            transferService.transfer(1.00, A123_ID, Z999_ID)
            fail("expected AccountNotFoundException")
        } catch (ex: AccountNotFoundException) {
        }

        assertThat(accountRepository.findById(A123_ID).getBalance(), CoreMatchers.equalTo(A123_INITIAL_BAL))
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testZeroTransferAmount() {
        try {
            transferService.transfer(0.00, A123_ID, C456_ID)
            fail("expected IllegalArgumentException")
        } catch (ex: IllegalArgumentException) {
        }
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testNegativeTransferAmount() {
        try {
            transferService.transfer(-100.00, A123_ID, C456_ID)
            fail("expected IllegalArgumentException")
        } catch (ex: IllegalArgumentException) {
        }
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun testTransferAmountLessThanOneCent() {
        try {
            transferService.transfer(0.009, A123_ID, C456_ID)
            fail("expected IllegalArgumentException")
        } catch (ex: IllegalArgumentException) {
        }
    }
}