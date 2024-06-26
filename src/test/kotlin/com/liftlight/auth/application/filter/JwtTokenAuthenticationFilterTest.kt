package com.liftlight.auth.application.filter

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.web.authentication.AuthenticationFailureHandler

class JwtTokenAuthenticationFilterTest {
    private val handler: AuthenticationFailureHandler = mock(AuthenticationFailureHandler::class.java)

    private val filter = JwtTokenAuthenticationFilter("/**", handler)
    private val request = MockHttpServletRequest()
    private val response = MockHttpServletResponse()

    @Test
    fun attemptAuthentication() {
        request.addHeader("Authorization", "Bearer token")

        Assertions.assertThatCode {
            filter.attemptAuthentication(request, response)
        }.doesNotThrowAnyException()

    }

    @Test
    fun successfulAuthentication() {
    }

    @Test
    fun unsuccessfulAuthentication() {
    }
}