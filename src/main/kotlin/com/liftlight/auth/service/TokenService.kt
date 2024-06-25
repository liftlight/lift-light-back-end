package com.liftlight.auth.service

import com.liftlight.auth.config.JwtExpiredTokenException
import com.liftlight.auth.domain.TokenParserResponse
import com.liftlight.auth.domain.Member.Role
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.crypto.SecretKey
import kotlin.collections.ArrayList

@Service
class TokenService(
    @Value("\${jwt.token.secret-key}") key: String,
    @param:Value("\${jwt.token.expTime}") private val expirationTime: Long,
    @param:Value("\${jwt.token.issuer}") private val issuer: String
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(key.toByteArray())

    fun createToken(username: String, authorities: List<GrantedAuthority>): String {
        val issuedAt = LocalDateTime.now()
        val expiredAt = issuedAt.plusMinutes(expirationTime)

        return Jwts.builder()
            .addClaims(createClaims(username, authorities.toCollection(ArrayList())))
            .setIssuer(issuer)
            .setIssuedAt(toDate(issuedAt))
            .setExpiration(toDate(expiredAt))
            .signWith(key)
            .compact()
    }

    @Throws(BadCredentialsException::class, JwtExpiredTokenException::class)
    fun parserToken(token: String?): TokenParserResponse {
        try {
            return tokenParserResponse(
                Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
            )
        } catch (ex: SignatureException) {
            throw BadCredentialsException("Invalid JWT token", ex)
        } catch (ex: UnsupportedJwtException) {
            throw BadCredentialsException("Invalid JWT token", ex)
        } catch (ex: MalformedJwtException) {
            throw BadCredentialsException("Invalid JWT token", ex)
        } catch (ex: IllegalArgumentException) {
            throw BadCredentialsException("Invalid JWT token", ex)
        } catch (expiredEx: ExpiredJwtException) {
            throw JwtExpiredTokenException("JWT Token expired", expiredEx)
        }
    }

    private fun tokenParserResponse(claimsJws: Jws<Claims>): TokenParserResponse {
        val username = claimsJws.body.subject
        val roles = claimsJws.body.get(AUTHORITIES_KEY, List::class.java)
            ?.map { Role.of(it as String) }
            ?.toList()
            ?: emptyList()


        return TokenParserResponse(username, roles)
    }

    private fun createClaims(username: String, authorities: ArrayList<GrantedAuthority>): Claims {
        val claims = Jwts.claims().setSubject(username)
        claims[AUTHORITIES_KEY] =
            authorities.stream().map { obj: GrantedAuthority -> obj.toString() }.toList()

        return claims
    }

    private fun toDate(dateTime: LocalDateTime): Date {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant())
    }

    companion object {
        private const val AUTHORITIES_KEY = "roles"
    }
}