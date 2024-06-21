package com.liftlight.infrastructure.exception

/**
 * 커스텀 예외
 */
class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
