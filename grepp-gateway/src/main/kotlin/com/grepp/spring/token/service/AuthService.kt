package com.grepp.spring.token.service

import com.grepp.spring.token.code.GrantType
import com.grepp.spring.token.util.JwtProvider
import com.grepp.spring.token.dto.AccessTokenDto
import com.grepp.spring.token.dto.TokenDto
import com.grepp.spring.token.entity.RefreshToken
import com.grepp.spring.token.repository.RefreshTokenRepository
import com.grepp.spring.token.repository.UserBlackListRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    val refreshTokenRepository: RefreshTokenRepository,
    val userBlackListRepository: UserBlackListRepository,
    val jwtProvider: JwtProvider
    ) {

    fun processTokenSignin(username: String, roles: String): TokenDto {
        // 블랙리스트에서 제거
        userBlackListRepository.deleteById(username)

        val dto: AccessTokenDto = jwtProvider.generateAccessToken(username, roles)
        val refreshToken = RefreshToken(
            email = username,
            accessTokenId = dto.id)
        refreshTokenRepository.save<RefreshToken>(refreshToken)

        return TokenDto(
            accessToken = dto.token,
            refreshToken = refreshToken.token,
            atExpiresIn = jwtProvider.atExpiration,
            rtExpiresIn = jwtProvider.rtExpiration,
            grantType = GrantType.BEARER
        )
    }
}
