package com.grepp.spring.infra.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class InternalAuthFilter(
    private val userDetailsService: UserDetailsServiceImpl
):OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val userId = request.getHeader("x-member-id")
        val roles = request.getHeader("x-member-role")
        val authorities:MutableSet<SimpleGrantedAuthority> = userDetailsService.findAuthorities(userId)

        userId ?: return
        roles?.let{
            authorities += SimpleGrantedAuthority(it)
        }
        val authentication = UsernamePasswordAuthenticationToken(userId,null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }
}