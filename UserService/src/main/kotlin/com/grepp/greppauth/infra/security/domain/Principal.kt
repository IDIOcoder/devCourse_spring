package com.grepp.greppauth.infra.auth.domain

import com.grepp.greppauth.app.model.member.entity.Member
import com.grepp.greppauth.infra.security.domain.Principal
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class Principal(
    username: String?, password: String?,
    authorities: Collection<GrantedAuthority?>?
) : User(username, password, authorities) {
    var accessToken: String? = null
    companion object {
        fun createPrincipal(
            member: Member,
            authorities: List<SimpleGrantedAuthority>
        ): Principal {
            return Principal(member.userId, member.password, authorities)
        }
    }
}
