package com.liftlight.user.model.dtos

import com.liftlight.user.enums.Country
import com.liftlight.user.model.entities.UserEntity

data class UserDTO(
    var id: Long,
    var nickName: String,
    var email: String,
    var rank: Long,
    var point: Long,
    var squat: Long,
    var benchPress: Long,
    var deadLift: Long,
    var country: Country,
    var profileImageUrl: String,
    var accessToken: String,
    var refreshToken: String
) {
    companion object {
        fun fromEntity(
            user: UserEntity,
            accessToken: String,
            refreshToken: String
        ): UserDTO {
            return UserDTO(
                id = user.id ?: 0L,
                nickName = user.nickName,
                email = user.email,
                rank = user.rank,
                point = user.point,
                squat = user.squat,
                benchPress = user.benchPress,
                deadLift = user.deadLift,
                country = user.country,
                profileImageUrl = user.profileImageUrl,
                accessToken = accessToken,
                refreshToken = refreshToken

            )
        }
    }
}
