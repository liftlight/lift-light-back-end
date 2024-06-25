package com.liftlight.auth.domain

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import java.util.*

@JvmInline
value class TokenValue(
    private val token: String
){

}

data class JwtToken(
    private val jwtToken: String,
    private val username: String,
    private val authorities: List<GrantedAuthority>
) : AbstractAuthenticationToken(authorities) {

    constructor(jwtToken: String) : this(jwtToken, "", emptyList())

    override fun getCredentials(): String {
        return this.jwtToken
    }

    override fun getPrincipal(): String {
        return username
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        if (!super.equals(o)) {
            return false
        }
        val that = o as JwtToken
        return Objects.equals(jwtToken, that.jwtToken) && Objects.equals(username, that.username)
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), jwtToken, username)
    }
}