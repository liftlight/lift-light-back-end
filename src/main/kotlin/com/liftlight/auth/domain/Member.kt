package com.liftlight.auth.domain

import jakarta.persistence.*
import java.util.*


@Entity
class Member(
    val username: String,
    val password: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER
) {


    enum class Role(
        private val title: String,
        private val value: String
    ) {
        USER("일반 사용자", "ROLE_USER"),
        ADMIN("관리자", "ROLE_ADMIN");

        companion object {
            fun of(value: String?): Role {
                return Arrays.stream(entries.toTypedArray())
                    .filter { role -> role.value == value }
                    .findFirst()
                    .orElseThrow { IllegalArgumentException("잘못된 권한입니다.") }
            }
        }
    }
}