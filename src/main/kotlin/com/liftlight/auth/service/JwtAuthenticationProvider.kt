package com.liftlight.auth.service

import com.liftlight.auth.domain.TokenParserResponse
import com.liftlight.auth.domain.JwtToken
import com.liftlight.auth.domain.Member
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
        return authenticate(authentication as JwtToken?)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return (JwtToken::class.java.isAssignableFrom(authentication))
    }

    fun authenticate(authentication: JwtToken): Authentication {
        val jwtToken: String = authentication.getCredentials()
        val response: TokenParserResponse = tokenService.parserToken(jwtToken)

        return JwtToken(jwtToken, response.username, authorities(response))
    }


    private fun authorities(response: TokenParserResponse): List<SimpleGrantedAuthority> {
        return response.roles
            .map { role -> SimpleGrantedAuthority(role as String) }
            .toList()
    }
}