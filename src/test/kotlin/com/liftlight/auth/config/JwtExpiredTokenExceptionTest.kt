package com.liftlight.auth.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JwtExpiredTokenExceptionTest{

    @Test
    fun `should create JwtExpiredTokenException with message and cause`(){
        val message = "message"
        val cause = Throwable()
        val jwtExpiredTokenException = JwtExpiredTokenException(message, cause)
        assertEquals(message, jwtExpiredTokenException.message)
        assertEquals(cause, jwtExpiredTokenException.cause)
    }
}
