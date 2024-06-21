package com.liftlight.user.application.commands

import com.liftlight.infrastructure.exception.CustomException
import com.liftlight.infrastructure.exception.ErrorCode
import com.liftlight.user.model.entities.UserEntity
import com.liftlight.user.repositories.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Spring Security에서 사용자 인증을 위해 사용되는 클래스
 */
@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val userEntity: UserEntity = getUser(email)
        val authorities = ArrayList(userEntity.role.authorities)

        return User.builder()
            .username(userEntity.email)
            .password(userEntity.password)
            .authorities(authorities)
            .build()
    }


    private fun getUser(email: String): UserEntity {
        return userRepository
            .findByEmail(email)
            .orElseThrow { CustomException(ErrorCode.USER_NOT_FOUND) }
    }
}