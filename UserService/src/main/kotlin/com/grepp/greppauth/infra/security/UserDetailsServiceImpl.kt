package com.grepp.greppauth.infra.auth

import com.grepp.greppauth.app.model.member.MemberRepository
import com.grepp.greppauth.app.model.member.entity.Member
import com.grepp.greppauth.infra.security.domain.Principal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserDetailsServiceImpl(
    private val memberRepository: MemberRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val member: Member = memberRepository.findById(username)
            .orElseThrow { UsernameNotFoundException(username) }

        val authorities: MutableList<SimpleGrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority(member.role.name))
        return Principal.createPrincipal(member, authorities)
    }

    fun findAuthorities(username: String?): List<SimpleGrantedAuthority> {
        val member = memberRepository.findById(username!!)
            .orElseThrow { UsernameNotFoundException(username) }
        val authorities: MutableList<SimpleGrantedAuthority> = java.util.ArrayList()
        authorities.add(SimpleGrantedAuthority(member.role.name))
        return authorities
    }
}