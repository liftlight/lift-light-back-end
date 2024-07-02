package com.liftlight.auth.application.filter

import com.liftlight.auth.domain.JwtToken
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.AuthenticationFailureHandler

class JwtTokenAuthenticationFilterTest {
    private val handler: AuthenticationFailureHandler = mock(AuthenticationFailureHandler::class.java)
    private val manager: AuthenticationManager = mock(AuthenticationManager::class.java)

    private val filter = JwtTokenAuthenticationFilter("/**", handler)
    private val request = MockHttpServletRequest()
    private val response = MockHttpServletResponse()

    @Test
    fun attemptAuthentication() = Assertions.assertThatCode {
        //given
        `when`(manager.authenticate(any())).thenAnswer { JwtToken("Test Token") }

        filter.setAuthenticationManager(manager)
        request.addHeader("Authorization", "Bearer token")
        filter.attemptAuthentication(request, response)
    }.doesNotThrowAnyException()


    @Test
    fun successfulAuthentication() = Assertions.assertThatCode {
        // static mock -> SecurityContextHolder.createEmptyContext()
        val mockFilterChain = MockFilterChain()
        filter.doFilter(request, response, mockFilterChain)
        mockFilterChain.doFilter(request, response)

    }.doesNotThrowAnyException()

    @Test
    fun unsuccessfulAuthentication() {
    }
}