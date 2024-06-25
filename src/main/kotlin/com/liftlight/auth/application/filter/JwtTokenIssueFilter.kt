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
    defaultFilterProcessesUrl: String,
    private val objectMapper: ObjectMapper,
    authenticationSuccessHandler: AuthenticationSuccessHandler?,
    authenticationFailureHandler: AuthenticationFailureHandler?
) : AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {
    init {
        this.setAuthenticationSuccessHandler(authenticationSuccessHandler)
        this.setAuthenticationFailureHandler(authenticationFailureHandler)
    }

    @Throws(AuthenticationException::class, IOException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        if (!isPostMethod(request)) {
            throw AuthMethodNotSupportedException("Authentication method not supported")
        }

        val loginRequest: SIgnIn = objectMapper.readValue(request.reader, SIgnIn::class.java)
        val token =
            UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username, loginRequest.password)

        return authenticationManager.authenticate(token)
    }

    private fun isPostMethod(request: HttpServletRequest): Boolean {
        return HttpMethod.POST.name() == request.method
    }
}

data class SIgnIn(
    val username: String, val password: String
) {
    init {
        require(username.isNotBlank()) { "Username must not be blank" }
        require(password.isNotBlank()) { "Password must not be blank" }
    }
}