package com.grepp.gateway.filter

import com.grepp.gateway.security.token.JwtProvider
import com.grepp.gateway.security.token.RefreshTokenService
import com.grepp.gateway.security.token.TokenCookieFactory
import com.grepp.gateway.security.token.code.TokenType
import com.grepp.gateway.security.token.dto.AccessTokenDto
import com.grepp.gateway.security.token.entity.RefreshToken
import com.grepp.gateway.security.token.entity.UserBlackList
import com.grepp.gateway.security.token.repository.UserBlackListRepository
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val refreshTokenService: RefreshTokenService,
    private val userBlackListRepository: UserBlackListRepository,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val requestAccessToken = jwtProvider.resolveToken(request, TokenType.ACCESS_TOKEN)
        if (requestAccessToken == null) {
            filterChain.doFilter(request, response)
            return
        }

        val claims = jwtProvider.parseClaim(requestAccessToken)
        if (userBlackListRepository.existsById(claims.subject)) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            if (jwtProvider.validateToken(requestAccessToken)) {
                filterChain.doFilter(createModifiedRequest(request, claims.subject,
                    "user"
                ), response)
                return
            }
        } catch (ex: ExpiredJwtException) {
            val newAccessToken: AccessTokenDto = renewingAccessToken(requestAccessToken, request)
            val newRefreshToken: RefreshToken? = refreshTokenService.renewingToken(claims.id, newAccessToken.id)

            newRefreshToken?:let{
                filterChain.doFilter(request, response)
                return
            }

            responseToken(response, newAccessToken, newRefreshToken)
        }

        filterChain.doFilter(request, response)
    }

    private fun responseToken(
        response: HttpServletResponse, newAccessToken: AccessTokenDto,
        newRefreshToken: RefreshToken
    ) {
        val accessTokenCookie =
            TokenCookieFactory.create(
                TokenType.ACCESS_TOKEN.name, newAccessToken.token,
                jwtProvider.rtExpiration
            )

        val refreshTokenCookie =
            TokenCookieFactory.create(
                TokenType.REFRESH_TOKEN.name, newRefreshToken.token,
                jwtProvider.rtExpiration
            )

        response.addHeader("Set-Cookie", accessTokenCookie.toString())
        response.addHeader("Set-Cookie", refreshTokenCookie.toString())
    }

    private fun renewingAccessToken(
        requestAccessToken: String,
        request: HttpServletRequest
    ): AccessTokenDto {
        val refreshToken = jwtProvider.resolveToken(request, TokenType.REFRESH_TOKEN)
        val claims = jwtProvider.parseClaim(requestAccessToken)

        val storedRefreshToken = refreshTokenService.findByAccessTokenId(claims.id)

        storedRefreshToken?.let{
            if (storedRefreshToken.token != refreshToken) {
                userBlackListRepository.save(UserBlackList(claims.subject))
            }
        }
        return jwtProvider.generateAccessToken(claims.subject)
    }

    private fun createModifiedRequest(
        request: HttpServletRequest,
        memberId: String,
        memberRole: String
    ): HttpServletRequestWrapper {
        return object : HttpServletRequestWrapper(request) {
            override fun getHeader(name: String): String {
                if (name.equals("X-Member-Id", ignoreCase = true)) {
                    return memberId
                } else if (name.equals("X-Member-Role", ignoreCase = true)) {
                    return memberRole
                }
                return super.getHeader(name)
            }
        }
    }
}