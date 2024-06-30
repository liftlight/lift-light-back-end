package com.liftlight.infrastructure.exception

import jakarta.xml.ws.http.HTTPException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*

enum class ErrorCode(val code: HttpStatus, val message: String) {
    UNDEFINED_EXCEPTION(INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),
    USER_ALREADY_EXISTS(BAD_REQUEST, "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_NOT_VERIFIED(BAD_REQUEST, "사용자가 인증되지 않았습니다."),
    DELETED_USER(BAD_REQUEST, "삭제된 사용자입니다."),
    INACTIVE_USER(BAD_REQUEST, "비활성화된 사용자입니다."),
}