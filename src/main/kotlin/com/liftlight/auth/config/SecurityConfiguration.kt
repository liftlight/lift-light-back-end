package com.liftlight.auth.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.liftlight.auth.application.filter.JwtTokenAuthenticationFilter
import com.liftlight.auth.application.filter.JwtTokenIssueFilter
import com.liftlight.auth.domain.Member.Role.ADMIN
import com.liftlight.auth.domain.Member.Role.USER
import com.liftlight.auth.service.JwtAuthenticationProvider
import com.liftlight.auth.service.JwtTokenIssueProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


private const val AUTHENTICATION_URL: String = "/api/auth/login"
private const val API_ROOT_URL: String = "/api/**"

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val successHandler: AuthenticationSuccessHandler,
    private val failureHandler: AuthenticationFailureHandler,
    private val objectMapper: ObjectMapper
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain =
        http.sessionManagement { it.sessionCreationPolicy(STATELESS) }.csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/say/admin").hasAnyRole(ADMIN.name)
                    .requestMatchers("/api/say/user").hasAnyRole(USER.name)
            }
            .addFilterBefore(
                jwtTokenIssueFilter(authenticationManager),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(
                jwtTokenAuthenticationFilter(authenticationManager),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(
        http: HttpSecurity,
        jwtTokenIssueProvider: JwtTokenIssueProvider?,
        jwtAuthenticationProvider: JwtAuthenticationProvider?
    ): AuthenticationManager = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        .also {
            it.authenticationProvider(jwtAuthenticationProvider)
            it.authenticationProvider(jwtTokenIssueProvider)
        }
        .build()


    private fun jwtTokenIssueFilter(authenticationManager: AuthenticationManager): JwtTokenIssueFilter = JwtTokenIssueFilter(AUTHENTICATION_URL, objectMapper, successHandler, failureHandler)
        .also {  it.setAuthenticationManager(authenticationManager)}

    private fun jwtTokenAuthenticationFilter(authenticationManager: AuthenticationManager): JwtTokenAuthenticationFilter = JwtTokenAuthenticationFilter(API_ROOT_URL, failureHandler)
        .also { it.setAuthenticationManager(authenticationManager) }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}