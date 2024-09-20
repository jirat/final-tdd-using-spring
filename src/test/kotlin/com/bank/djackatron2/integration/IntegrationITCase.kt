package com.bank.djackatron2.integration

import com.bank.djackatron2.domain.InsufficientFundsException
import com.bank.djackatron2.repository.internal.JdbcAccountRepository
import com.bank.djackatron2.service.internal.DefaultTransferService
import com.bank.djackatron2.service.internal.ZeroFeePolicy
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import javax.sql.DataSource


class IntegrationITCase {

    @Bean
    fun dataSource(): DataSource {
        return EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:jdbc/schema.sql")
            .addScript("classpath:jdbc/test-data.sql").build()
    }

    @Test
    @Throws(InsufficientFundsException::class)
    fun transferTenDollars() {
        val feePolicy = ZeroFeePolicy()
        val accountRepository = JdbcAccountRepository(JdbcTemplate(dataSource()))
        val transferService = DefaultTransferService(accountRepository, feePolicy)

        assertThat(accountRepository.findById("A123").getBalance(), CoreMatchers.equalTo(100.00))
        assertThat(accountRepository.findById("C456").getBalance(), CoreMatchers.equalTo(0.00))

        transferService.transfer(10.00, "A123", "C456")

        assertThat(accountRepository.findById("A123").getBalance(), CoreMatchers.equalTo(90.00))
        assertThat(accountRepository.findById("C456").getBalance(), CoreMatchers.equalTo(10.00))
    }

}

