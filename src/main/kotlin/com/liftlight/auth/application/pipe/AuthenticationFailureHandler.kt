package com.liftlight.auth.application.pipe

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.io.IOException


@Component
class DefaultAuthenticationFailureHandler : AuthenticationFailureHandler {
    private val objectMapper: ObjectMapper? = null

    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest?, response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        objectMapper!!.writeValue(response.writer, exception.message?.let { ErrorResponse(it) })
    }

    private data class ErrorResponse(val message: String)
}