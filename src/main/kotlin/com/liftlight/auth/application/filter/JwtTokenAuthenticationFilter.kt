package com.liftlight.auth.application.filter

import com.liftlight.auth.domain.JwtToken
import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder.setContext
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import java.util.Objects.isNull


class JwtTokenAuthenticationFilter(matcher: String, failureHandler: AuthenticationFailureHandler) :
    AbstractAuthenticationProcessingFilter(matcher) {

    init {
        this.setAuthenticationFailureHandler(failureHandler)
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication =
        request.getHeader(HttpHeaders.AUTHORIZATION)
            .takeUnless { header -> isNull(header) || !header.startsWith("Bearer ") }
            ?.let { JwtToken(it) }
            ?.let { authenticationManager.authenticate(it) }
            ?: let { throw BadCredentialsException("Invalid token") }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain,
        authentication: Authentication?
    ) = SecurityContextHolder.createEmptyContext()
        .also { it.authentication = authentication }
        .also { context -> setContext(context) }
        .let { chain.doFilter(request, response) }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?, response: HttpServletResponse?,
        authenticationException: AuthenticationException?
    ) = SecurityContextHolder.clearContext()
        .let { failureHandler.onAuthenticationFailure(request, response, authenticationException) }


}