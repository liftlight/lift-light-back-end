package com.liftlight.user.application.commands

import com.liftlight.infrastructure.exception.CustomException
import com.liftlight.infrastructure.exception.ErrorCode.USER_ALREADY_EXISTS
import com.liftlight.user.model.entities.UserEntity
import com.liftlight.user.presentation.request.UserSignUpRequest
import com.liftlight.user.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserSignUpService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    fun registerUser(userSignUpRequest: UserSignUpRequest): UserEntity {
        userRepository
            .findByEmail(userSignUpRequest.email)
            .ifPresent { throw CustomException(USER_ALREADY_EXISTS) }

        val userEntity = UserEntity.fromRequestAndPassword(
            userSignUpRequest,
            passwordEncoder.encode(userSignUpRequest.password)
        )
        return userRepository.save(userEntity)
    }
}
