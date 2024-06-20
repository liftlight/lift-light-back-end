package com.liftlight.user.application.commands

import com.liftlight.user.model.entities.UserEntity
import com.liftlight.user.presentation.request.UserSignUpRequest
import com.liftlight.user.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserSignUpService( // TODO: BCryptPasswordEncoder 빈 등록
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    fun registerUser(userSignUpRequest: UserSignUpRequest): UserEntity {
        userRepository.findByEmail(userSignUpRequest.email).ifPresent {
            // TODO: CustomException 생성
            throw CustomException(userSignUpRequest.email)
        }

        val encodedPassword = passwordEncoder.encode(userSignUpRequest.password)
        val userEntity = UserEntity.fromRequestAndPassword(
            userSignUpRequest,
            encodedPassword
        )
        return userRepository.save(userEntity)
    }
}
