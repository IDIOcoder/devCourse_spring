package com.grepp.auth.app.model.member.domain

import com.grepp.spring.app.model.member.entity.Member
import org.springframework.security.core.userdetails.User

class Principal(
    username: String?, password: String?,
    authorities: Collection<GrantedAuthority?>?
) : User(username, password, authorities) {
    companion object {
        fun createPrincipal(
            member: Member,
            authorities: List<SimpleGrantedAuthority?>?
        ): Principal {
            return Principal(member.getUserId(), member.getPassword(), authorities)
        }
    }
}
