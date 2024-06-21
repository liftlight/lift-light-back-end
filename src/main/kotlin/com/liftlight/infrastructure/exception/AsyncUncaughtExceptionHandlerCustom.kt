package com.liftlight.infrastructure.exception

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.slf4j.LoggerFactory
import java.lang.reflect.Method

/**
 * 비동기 작업에서 발생한 예외를 처리하는 [AsyncUncaughtExceptionHandler]
 */
class AsyncUncaughtExceptionHandlerCustom : AsyncUncaughtExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handleUncaughtException(ex: Throwable, method: Method, vararg params: Any?) {
        log.error("Async Exception - ${ex.message}")
        log.error("Async Method - ${method.name}")
        params.forEach { param -> log.error("Parameter value - $param") }
        throw RuntimeException(ex.message)
    }
}
