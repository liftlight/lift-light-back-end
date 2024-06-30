package com.liftlight.auth.application.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.liftlight.auth.config.AuthMethodNotSupportedException
import io.jsonwebtoken.io.IOException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler


class JwtTokenIssueFilter(
    private val defaultFilterProcessesUrl: String,
    private val objectMapper: ObjectMapper,
    private val authenticationSuccessHandler: AuthenticationSuccessHandler,
    private val authenticationFailureHandler: AuthenticationFailureHandler
) : AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {
    init {
        this.setAuthenticationSuccessHandler(authenticationSuccessHandler)
        this.setAuthenticationFailureHandler(authenticationFailureHandler)
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication =
        takeIf { HttpMethod.POST.name() == request.method }
            ?.let { objectMapper.readValue(request.reader, SIgnIn::class.java) }
            ?.let { UsernamePasswordAuthenticationToken.unauthenticated(it.username, it.password) }
            ?.let { authenticationManager.authenticate(it) }
            ?: let { throw AuthMethodNotSupportedException("Authentication method not supported") }

}

private data class SIgnIn(
    val username: String, val password: String
) {
    init {
        require(username.isNotBlank()) { "Username must not be blank" }
        require(password.isNotBlank()) { "Password must not be blank" }
    }
}