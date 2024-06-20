package com.liftlight.user.model.entities

import com.liftlight.infrastructure.BaseEntity
import com.liftlight.user.enums.Country
import com.liftlight.user.enums.UserRole
import com.liftlight.user.enums.UserStatus
import com.liftlight.user.presentation.request.UserSignUpRequest
import jakarta.persistence.*

@Entity
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var nickName: String,
    var email: String,
    var password: String,

    @Enumerated(EnumType.STRING)
    var role: UserRole,

    @Enumerated(EnumType.STRING)
    var status: UserStatus,

    var rank: Long = 0,
    var squat: Long = 0,
    var point: Long = 0,
    var benchPress: Long = 0,
    var deadLift: Long = 0,

    @Enumerated(EnumType.STRING)
    var country: Country,

    var profileImageUrl: String
) : BaseEntity() {
    companion object {
        fun fromRequestAndPassword(
            userSignUpRequest: UserSignUpRequest,
            password: String
        ): UserEntity {
            return UserEntity(
                nickName = userSignUpRequest.nickName,
                email = userSignUpRequest.email,
                password = password,
                role = UserRole.ROLE_USER,
                status = UserStatus.PENDING,
                rank = 0,
                point = 0,
                squat = 0,
                benchPress = 0,
                deadLift = 0,
                country = userSignUpRequest.country,
                profileImageUrl = userSignUpRequest.profileImageUrl
            )
        }
    }
}
