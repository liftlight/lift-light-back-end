package com.liftlight.infrastructure.exception

enum class ErrorCode(val code: String, val message: String) {
    UNDEFINED_EXCEPTION("UNDEFINED_EXCEPTION", "알 수 없는 오류가 발생했습니다."),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다.")
}