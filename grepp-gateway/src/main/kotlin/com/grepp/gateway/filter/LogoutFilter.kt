package com.grepp.gateway.filter

import com.grepp.gateway.security.token.JwtProvider
import com.grepp.gateway.security.token.RefreshTokenService
import com.grepp.gateway.security.token.TokenCookieFactory
import com.grepp.gateway.security.token.code.TokenType
import io.jsonwebtoken.Claims
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class LogoutFilter(
    private val refreshTokenService: RefreshTokenService,
    private val jwtTokenProvider: JwtProvider
) : OncePerRequestFilter() {

    protected override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken: String? = jwtTokenProvider.resolveToken(request, TokenType.ACCESS_TOKEN)

        accessToken?:let{
            filterChain.doFilter(request, response)
            return
        }

        val path = request.requestURI
        val claims: Claims = jwtTokenProvider.parseClaim(accessToken)

        if (path != "/logout/oauth2/code/grepp") {
            filterChain.doFilter(request, response)
            return
        }

        refreshTokenService.deleteByAccessTokenId(claims.id)
        SecurityContextHolder.clearContext()
        val expiredAccessToken: ResponseCookie =
            TokenCookieFactory.createExpiredToken(TokenType.ACCESS_TOKEN)
        val expiredRefreshToken: ResponseCookie =
            TokenCookieFactory.createExpiredToken(TokenType.REFRESH_TOKEN)
        val expiredSessionId: ResponseCookie =
            TokenCookieFactory.createExpiredToken(TokenType.AUTH_SERVER_SESSION_ID)
        response.addHeader("Set-Cookie", expiredAccessToken.toString())
        response.addHeader("Set-Cookie", expiredRefreshToken.toString())
        response.addHeader("Set-Cookie", expiredSessionId.toString())
        response.sendRedirect("/")
        filterChain.doFilter(request, response)
    }
}
