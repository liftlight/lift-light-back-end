package com.liftlight.infrastructure.exception

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 처리
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidException(exception: MethodArgumentNotValidException): ResponseEntity<List<String>> {
        val errors = exception.bindingResult
            .allErrors
            .mapNotNull { it.defaultMessage }
        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(exception: CustomException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            code = exception.errorCode.code,
            message = exception.errorCode.message
        )
        log.error(exception.message, exception)
        return ResponseEntity.badRequest().body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleUndefinedException(exception: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            code = ErrorCode.UNDEFINED_EXCEPTION.code,
            message = exception.message ?: "알 수 없는 오류가 발생했습니다."
        )
        log.error(exception.message, exception)
        return ResponseEntity.badRequest().body(errorResponse)
    }
}
