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
    var profileImageUrl: String
)

fun UserEntity.toDTO(): UserDTO {
    return UserDTO(
        id = this.id ?: 0L,
        nickName = this.nickName,
        email = this.email,
        rank = this.rank,
        point = this.point,
        squat = this.squat,
        benchPress = this.benchPress,
        deadLift = this.deadLift,
        country = this.country,
        profileImageUrl = this.profileImageUrl
    )
}
