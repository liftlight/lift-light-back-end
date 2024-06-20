package com.liftlight.user.presentation.request

import com.liftlight.user.enums.Country

data class UserSignUpRequest(
    var id: Long,
    var nickName: String,
    var email: String,
    var password: String,
    var country: Country,
    var profileImageUrl: String
)
