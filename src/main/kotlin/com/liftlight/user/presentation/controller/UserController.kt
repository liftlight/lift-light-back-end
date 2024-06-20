package com.liftlight.user.presentation.controller

import com.liftlight.user.application.commands.UserSignUpService
import com.liftlight.user.presentation.request.UserSignUpRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(@Autowired val userSignUpService: UserSignUpService) {

    @PostMapping("/register")
    fun registerUser(
        @RequestBody userSignUpRequest: UserSignUpRequest,
    ): ResponseEntity<Void> {
        userSignUpService.registerUser(userSignUpRequest)
        return ResponseEntity.status(CREATED).build()
    }
}
