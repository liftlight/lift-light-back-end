package com.liftlight.infrastructure.filters

import com.liftlight.auth.domain.dto.TokenIssuanceDTO
import com.liftlight.user.application.commands.UserDetailsServiceImpl
import com.liftlight.user.enums.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.sql.Date
import javax.crypto.SecretKey

/**
 * JWT 토큰을 생성하고 검증하는 클래스
 */
@Component
class JwtProvider(private val userDetailsService: UserDetailsServiceImpl) {

    @Value("\${token.issuer}")
    private lateinit var issuer: String

    @Value("\${token.secret-key}")
    private lateinit var secretKeyString: String
    private lateinit var secretKey: SecretKey
    private val log = LoggerFactory.getLogger(JwtProvider::class.java)

    @PostConstruct
    fun init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString))
    }

    fun issueAccessToken(tokenIssuanceDto: TokenIssuanceDTO): String {
        val claims: Claims = Jwts.claims().setSubject(tokenIssuanceDto.id.toString())
        claims["email"] = tokenIssuanceDto.email
        claims["userRole"] = tokenIssuanceDto.userRole
        claims["country"] = tokenIssuanceDto.country.name

        return buildJwt(claims)
    }

    fun issueRefreshToken(): String {
        return buildJwt(null)
    }

    fun validateToken(token: String?): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: JwtException) {
            false
        }
    }

    fun getIdFromToken(token: String): Long {
        val cleanToken = removeBearer(token)

        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(cleanToken)
            .body
            .subject
            .toLong()
    }

    fun getEmailFromToken(token: String): String {
        val cleanToken = removeBearer(token)

        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(cleanToken)
            .body
            .get("email", String::class.java)
    }

    fun getAuthentication(token: String): Authentication {
        val cleanToken = removeBearer(token)

        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(cleanToken)
            .body

        val email: String = claims.get("email", String::class.java)
        val userRole: UserRole = UserRole.valueOf(claims.get("userRole", String::class.java))

        val userDetails: UserDetails = userDetailsService.loadUserByUsername(email)

        val authorities: MutableList<GrantedAuthority> = ArrayList(userDetails.authorities)
        authorities.addAll(userRole.authorities) // 추가된 역할 권한

        return UsernamePasswordAuthenticationToken(userDetails, "", authorities)
    }

    private fun removeBearer(token: String): String {
        return if (token.startsWith("Bearer ")) {
            token.substring(7)
        } else token
    }

    private fun buildJwt(claims: Claims?): String {
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(issuer)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + if (claims == null) SIX_MONTH else ONE_DAY))
            .signWith(secretKey)
            .compact()
    }

    companion object {
        private const val ONE_DAY = 24 * 60 * 60 * 1000L
        private const val SIX_MONTH = 6 * 30 * 24 * 60 * 60 * 1000L
    }
}
