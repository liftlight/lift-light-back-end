package com.liftlight.user.application.commands

import com.liftlight.auth.domain.dto.TokenIssuanceDTO
import com.liftlight.infrastructure.RedisService
import com.liftlight.infrastructure.exception.CustomException
import com.liftlight.infrastructure.exception.ErrorCode.*
import com.liftlight.infrastructure.filters.JwtProvider
import com.liftlight.user.enums.UserStatus
import com.liftlight.user.enums.UserStatus.*
import com.liftlight.user.model.dtos.UserDTO
import com.liftlight.user.model.entities.UserEntity
import com.liftlight.user.presentation.request.UserSignInRequest
import com.liftlight.user.repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserSignInService(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val redisService: RedisService
) {
    @Transactional
    fun signInUser(userSignUpRequest: UserSignInRequest): UserDTO {
        val user = getUser(userSignUpRequest.email)
        validatePassword(userSignUpRequest, user)
        validateUserStatus(user.status)
        val tokenPair = getTokens(TokenIssuanceDTO.from(user))
        storeRefreshToken(user.email, tokenPair.second)
        return UserDTO.fromEntity(user, tokenPair.first, tokenPair.second)
    }

    private fun validateUserStatus(userStatus: UserStatus) {
        when (userStatus) {
            PENDING -> throw CustomException(USER_NOT_VERIFIED)
            DELETED -> throw CustomException(DELETED_USER)
            INACTIVE -> throw CustomException(INACTIVE_USER)
            ACTIVE -> return
        }
    }

    private fun storeRefreshToken(userEmail: String, refreshToken: String) {
        redisService.saveKeyAndValue(
            TOKEN_PREFIX + userEmail,
            refreshToken,
            REFRESH_TOKEN_EXPIRE_TIME
        )
    }

    private fun getTokens(
        tokenIssuanceDto: TokenIssuanceDTO
    ): Pair<String, String> {
        return Pair(
            jwtProvider.issueAccessToken(tokenIssuanceDto),
            jwtProvider.issueRefreshToken()
        )
    }

    private fun validatePassword(
        userSignUpRequest: UserSignInRequest,
        user: UserEntity
    ) {
        if (!passwordEncoder.matches(userSignUpRequest.password, user.password))
            throw CustomException(USER_NOT_FOUND)
    }

    private fun getUser(email: String): UserEntity {
        return userRepository
            .findByEmail(email)
            .orElseThrow { throw CustomException(USER_NOT_FOUND) }
    }
}

const val REFRESH_TOKEN_EXPIRE_TIME: Int = 5 * 29 * 24 * 60
const val TOKEN_PREFIX: String = "Refresh: "
