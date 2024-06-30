package com.liftlight.auth.service

import com.liftlight.auth.domain.JwtToken
import com.liftlight.auth.domain.TokenParserResponse
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component


@Component
class JwtAuthenticationProvider(
    private val tokenService: TokenService
) : AuthenticationProvider {


    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication?): Authentication {
        if (authentication == null) {
            throw IllegalArgumentException("Authentication is null")
        }
        val token = authentication as JwtToken

        val jwtToken: String = token.credentials
        val response: TokenParserResponse = tokenService.parserToken(jwtToken)

        return JwtToken(jwtToken, response.username, authorities(response))
    }

    override fun supports(authentication: Class<*>): Boolean {
        return (JwtToken::class.java.isAssignableFrom(authentication))
    }

    private fun authorities(response: TokenParserResponse): List<SimpleGrantedAuthority> {
        return response.roles
            .map { role -> SimpleGrantedAuthority(role as String) }
            .toList()
    }
}