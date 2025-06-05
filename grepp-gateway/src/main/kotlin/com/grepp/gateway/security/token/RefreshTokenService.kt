package com.grepp.gateway.security.token

import com.grepp.gateway.security.token.entity.RefreshToken
import com.grepp.gateway.security.token.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Consumer

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun deleteByAccessTokenId(id: String) {
        val refreshToken = refreshTokenRepository.findByAccessTokenId(id)
        refreshToken?.let{
            refreshTokenRepository.deleteById( it.id)
        }
    }

    fun renewingToken(id: String, newTokenId: String?): RefreshToken? {
        val refreshToken = findByAccessTokenId(id)
        refreshToken?.let{
            refreshToken.token = UUID.randomUUID().toString()
            refreshToken.accessTokenId = newTokenId
            refreshTokenRepository.save(refreshToken)
        }
        return refreshToken
    }

    fun findByAccessTokenId(id: String): RefreshToken? {
        return refreshTokenRepository.findByAccessTokenId(id)
    }
}
