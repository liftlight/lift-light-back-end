package com.liftlight.auth.service

import com.liftlight.auth.domain.Member
import com.liftlight.auth.domain.MemberRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class UserDetailsFromDbService(
    private val memberRepository: MemberRepository
) : UserDetailsService {


    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails = memberRepository.findByUsername(username)
        ?.toUser()
        ?: throw UsernameNotFoundException("User not found")

    private fun Member.toUser() = User(username, password, listOf(SimpleGrantedAuthority(role.name)))
}
