package com.liftlight.user.enums

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class UserRole(val roleName: String, val authorities: List<GrantedAuthority>) {
    ROLE_USER("ROLE_USER", listOf(SimpleGrantedAuthority("ROLE_USER"))),
    ROLE_ADMIN("ROLE_ADMIN", listOf(SimpleGrantedAuthority("ROLE_ADMIN")));
}
