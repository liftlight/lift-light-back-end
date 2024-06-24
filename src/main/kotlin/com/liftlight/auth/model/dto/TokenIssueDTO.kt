package com.liftlight.auth.model.dto

import com.liftlight.user.enums.Country
import com.liftlight.user.enums.UserRole
import com.liftlight.user.model.entities.UserEntity

data class TokenIssuanceDTO(
    val id: Long?,
    val email: String?,
    val userRole: UserRole?,
    val country: Country
) {
    companion object {
        fun from(userEntity: UserEntity): TokenIssuanceDTO {
            return TokenIssuanceDTO(
                id = userEntity.id,
                email = userEntity.email,
                userRole = userEntity.role,
                country = userEntity.country
            )
        }
    }
}
