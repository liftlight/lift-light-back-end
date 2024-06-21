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

@Component
class JWTFilter : OncePerRequestFilter() {
    private val jwtProvider: JwtProvider? = null

    /**
     * 주어진 URI가 화이트리스트에 있는지 여부를 판별
     * @param requestURI 현재 요청의 URI
     * @return 화이트리스트에 포함되어 있지 않으면 true, 포함되어 있으면 false 반환
     */
    private fun isFilterCheck(requestURI: String): Boolean {
        return !PatternMatchUtils.simpleMatch(
            ALL_WHITELIST, requestURI
        )
    }

    /**
     * HTTP 요청에서 헤더를 통해 JWT 토큰 추출
     * @param request 현재의 HTTP 요청
     * @return 추출된 JWT 토큰 또는 null
     */
    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken: String = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }

    /**
     * 실제 필터 로직이 구현된 메서드로, 토큰을 추출하고 검증하여 사용자 인증,
     * 화이트리스트에 있는 경우 필터를 건너뛰어 다음 필터로 진행
     *
     * @param request 현재의 HTTP 요청
     * @param response HTTP 응답
     * @param chain 다음 필터로 전달하기 위한 FilterChain 객체
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val token = extractTokenFromRequest(request)

        try {
            // 화이트리스트에 있는 경우에는 필터링을 건너뛰어서 다음 필터로 진행
            if (isFilterCheck(request.getRequestURI())) {
                // 화이트리스트에 없는 경우에만 검증 처리
                if (token != null) {
                    SecurityContextHolder
                        .getContext().authentication =
                        jwtProvider?.getAuthentication(token)
                }
            }
            chain.doFilter(request, response)
        } catch (e: Exception) {
            e.printStackTrace()
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
        }
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
