package com.grepp.gateway.security

import com.grepp.gateway.security.token.JwtProvider
import com.grepp.gateway.security.token.code.GrantType
import com.grepp.gateway.security.token.dto.AccessTokenDto
import com.grepp.gateway.security.token.dto.TokenDto
import com.grepp.gateway.security.token.entity.RefreshToken
import com.grepp.gateway.security.token.repository.RefreshTokenRepository
import com.grepp.gateway.security.token.repository.UserBlackListRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtTokenProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userBlackListRepository: UserBlackListRepository
) {

    fun processTokenSignin(email: String): TokenDto {
        userBlackListRepository.deleteById(email)
        val accessToken: AccessTokenDto = jwtTokenProvider.generateAccessToken(email)
        val refreshToken: RefreshToken = RefreshToken(accessToken.id)
        refreshTokenRepository.save(refreshToken)

        return TokenDto(
            accessToken = accessToken.token,
            atId = accessToken.id,
            refreshToken = refreshToken.token,
            grantType = GrantType.BEARER,
            rtExpiresIn = jwtTokenProvider.rtExpiration,
            atExpiresIn = jwtTokenProvider.atExpiration,
        )
    }
}
