package com.grepp.gateway.security.token

import com.grepp.gateway.security.token.code.TokenType
import com.grepp.gateway.security.token.dto.TokenDto
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie

class TokenResponseExecutor {
    companion object{
        fun response(
            response: HttpServletResponse,
            tokenDto: TokenDto
        ):HttpServletResponse{
            val accessTokenCookie: ResponseCookie = TokenCookieFactory.create(
                TokenType.ACCESS_TOKEN.name,
                tokenDto.accessToken, tokenDto.atExpiresIn
            )

            val refreshTokenCookie: ResponseCookie = TokenCookieFactory.create(
                TokenType.REFRESH_TOKEN.name,
                tokenDto.refreshToken, tokenDto.rtExpiresIn
            )
            response.addHeader("Set-Cookie", accessTokenCookie.toString())
            response.addHeader("Set-Cookie", refreshTokenCookie.toString())
            return response
        }
    }
}