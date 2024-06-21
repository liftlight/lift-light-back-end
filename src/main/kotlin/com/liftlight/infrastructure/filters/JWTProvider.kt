package com.liftlight.infrastructure.filters

import com.liftlight.auth.model.dto.TokenIssuanceDto
import com.liftlight.user.application.commands.UserDetailsServiceImpl
import com.liftlight.user.enums.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
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
@Slf4j
@Component
class JwtProvider(private val userDetailsService: UserDetailsServiceImpl) {

    @Value("\${token.issuer}")
    private val issuer: String? = null
    private var secretKey: SecretKey? = null

    @Value("\${token.secret-key}")
    private val secretKeyString: String? = null

    @PostConstruct
    fun init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString))
    }

    /**
     * 주어진 TokenIssuanceDto를 사용하여 액세스 토큰을 발급합니다.
     *
     * @param tokenTokenIssuanceDto 액세스 토큰 발급에 필요한 정보를 담은 TokenIssuanceDto
     * @return 발급된 액세스 토큰
     */
    fun issueAccessToken(tokenTokenIssuanceDto: TokenIssuanceDto): String {
        val claims: Claims =
            Jwts.claims().setSubject(tokenTokenIssuanceDto.id.toString())
        claims.put("email", tokenTokenIssuanceDto.email)
        claims.put("userRole", tokenTokenIssuanceDto.userRole)
        claims.put("country", tokenTokenIssuanceDto.country.name)

        return buildJwt(claims)
    }

    fun issueRefreshToken(): String {
        return buildJwt(null)
    }

    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: JwtException) {
            return false
        }
    }

    /**
     * 주어진 토큰에서 사용자 ID 추출
     *
     * @param token 추출할 사용자 ID가 포함된 엑세스 토큰
     * @return 추출된 사용자 ID
     */
    fun getIdFromToken(token: String): Long {
        var token = token
        token = removeBearer(token)

        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject().toLong()
    }

    /**
     * 주어진 토큰에서 이메일 추출
     *
     * @param token 추출할 이메일이 포함된 엑세스 토큰
     * @return 추출된 이메일
     */
    fun getEmailFromToken(token: String): String {
        var token = token
        token = removeBearer(token)

        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("email", String::class.java)
    }

    /**
     * 주어진 토큰을 사용하여 인증 객체 생성
     *
     * @param token 사용하여 인증을 수행할 토큰
     * @return 생성된 Authentication 객체
     */
    fun getAuthentication(token: String): Authentication {
        var token = token
        token = removeBearer(token)

        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody()

        val email: String = claims.get("email", String::class.java)
        val userRole: UserRole =
            UserRole.valueOf(claims.get("userRole", String::class.java))

        val userDetails: UserDetails =
            userDetailsService.loadUserByUsername(email)

        val authorities: MutableList<GrantedAuthority> =
            ArrayList(userDetails.authorities)
        authorities.addAll(userRole.authorities) // 추가된 역할 권한

        return UsernamePasswordAuthenticationToken(
            userDetails, "", authorities
        )
    }

    /**
     * 주어진 토큰에서 "Bearer " 접두어를 제거한 토큰 반환
     *
     * @param token 제거할 "Bearer " 접두어가 포함된 토큰
     * @return "Bearer " 접두어가 제거된 토큰
     */
    private fun removeBearer(token: String): String {
        if (token.startsWith("Bearer ")) {
            return token.substring(7)
        }
        return token
    }

    /**
     * 주어진 Claims를 사용하여 JWT 생성
     *
     * @param claims JWT에 포함될 클레임 정보
     * @return 생성된 JWT
     */
    private fun buildJwt(claims: Claims?): String {
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(issuer)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(
                Date(
                    System.currentTimeMillis() +
                            (if (claims == null) SIX_MONTH else ONE_DAY)
                )
            )
            .signWith(secretKey)
            .compact()
    }

    companion object {
        private const val ONE_DAY = 24 * 60 * 60 * 1000L
        private const val SIX_MONTH = 6 * 30 * 24 * 60 * 60 * 1000L
    }
}