package com.liftlight.user.application.commands

import com.liftlight.infrastructure.exception.CustomException
import com.liftlight.infrastructure.exception.ErrorCode.USER_ALREADY_EXISTS
import com.liftlight.user.model.entities.UserEntity
import com.liftlight.user.presentation.request.UserSignUpRequest
import com.liftlight.user.repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserSignUpService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    @Transactional
    fun registerUser(userSignUpRequest: UserSignUpRequest) {
        checkIfItsExistingUSer(userSignUpRequest.email)
        val encodedPassword = passwordEncoder.encode(userSignUpRequest.password)
        userRepository.save(
            createUserWithRequest(userSignUpRequest, encodedPassword)
        )
    }

    private fun createUserWithRequest(
        userSignUpRequest: UserSignUpRequest,
        password: String
    ) = (UserEntity.fromRequestAndPassword(userSignUpRequest, password));

    private fun checkIfItsExistingUSer(email: String) {
        userRepository
            .findByEmail(email)
            .ifPresent { throw CustomException(USER_ALREADY_EXISTS) }
    }
}
