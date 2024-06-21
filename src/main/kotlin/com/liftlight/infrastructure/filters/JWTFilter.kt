package com.liftlight.infrastructure.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.PatternMatchUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * JWT 토큰을 추출/검증 필터
 */
@Component
class JWTFilter(private val jwtProvider: JwtProvider) : OncePerRequestFilter() {

    private fun isFilterCheck(requestURI: String): Boolean {
        return !PatternMatchUtils.simpleMatch(ALL_WHITELIST, requestURI)
    }

    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION)
        return if (!bearerToken.isNullOrBlank() && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    @Throws(IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val requestURI = request.requestURI
        if (isFilterCheck(requestURI)) {
            val token = extractTokenFromRequest(request)
            token?.let {
                try {
                    val authentication = jwtProvider.getAuthentication(it)
                    SecurityContextHolder.getContext().authentication =
                        authentication
                } catch (e: Exception) {
                    e.printStackTrace()
                    response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        e.message
                    )
                    return
                }
            }
        }
        chain.doFilter(request, response)
    }

    companion object {
        private val ALL_WHITELIST = arrayOf(
            "/api/v1/users/**",
            "/api/v1/locations",
            "/ws/**",
            "/swagger-ui/index.html",
            "/swagger-ui/**",
            "/v2/api-docs",
            "/swagger-resources/**",
            "/webjars/**"
        )
    }
}
