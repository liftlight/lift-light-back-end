package com.liftlight.auth.service

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component


@Component
class JwtTokenIssueProvider(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {

    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication =
        authenticate(authentication.principal as String, authentication.principal as String)

    override fun supports(authentication: Class<*>): Boolean =
        (UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication))

    private fun authenticate(username: String, password: String): UsernamePasswordAuthenticationToken =
        userDetailsService.loadUserByUsername(username)
            .takeIf { passwordEncoder.matches(password, it.password) }
            ?.let { return authenticated(it.username, null, authorities(it)) }
            ?: throw BadCredentialsException("[JwtTokenIssueProvider] Invalid username or password for user: [$username]")

    private fun authorities(user: UserDetails): List<SimpleGrantedAuthority> = user.authorities
        .map { obj: GrantedAuthority -> obj.authority }
        .map { role: String? -> SimpleGrantedAuthority(role) }
        .toList()
}