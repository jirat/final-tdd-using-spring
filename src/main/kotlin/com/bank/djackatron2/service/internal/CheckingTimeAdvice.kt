package com.bank.djackatron2.service.internal

import com.bank.djackatron2.service.OutOfServiceException
import com.bank.djackatron2.service.TimeService
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import mu.KotlinLogging
import org.springframework.web.server.MethodNotAllowedException

import java.time.LocalTime

private val logger = KotlinLogging.logger {}

class CheckingTimeAdvice(
    private val timeService: TimeService
): MethodInterceptor {

    override fun invoke(invocation: MethodInvocation): Any? {
        logger.info { "Checking Time Service" }
        if (timeService.isServiceAvailable(LocalTime.now())) {
            return invocation.proceed()
        } else {
            throw OutOfServiceException()
        }
    }

}