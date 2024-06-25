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
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import java.util.Objects.isNull


class JwtTokenAuthenticationFilter(matcher: String, failureHandler: AuthenticationFailureHandler) :
    AbstractAuthenticationProcessingFilter(matcher) {

    init {
        this.setAuthenticationFailureHandler(failureHandler)
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val tokenPayload = extractToken(request.getHeader(HttpHeaders.AUTHORIZATION))

        return authenticationManager.authenticate(JwtToken(tokenPayload))
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain,
        authentication: Authentication?
    ) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication

        SecurityContextHolder.setContext(context)
        chain.doFilter(request, response)
    }

    @Throws(IOException::class, ServletException::class)
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?, response: HttpServletResponse?,
        authenticationException: AuthenticationException?
    ) {
        SecurityContextHolder.clearContext()
        failureHandler.onAuthenticationFailure(request, response, authenticationException)
    }

    private fun extractToken(tokenPayload: String): String {
        if (isNull(tokenPayload) || !tokenPayload.startsWith("Bearer ")) {
            throw BadCredentialsException("Invalid token")
        }
        return tokenPayload.replace("Bearer ", "")
    }
}