package com.grepp.spring.token.service

import com.grepp.spring.token.entity.RefreshToken
import com.grepp.spring.token.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Consumer

@Service
class RefreshTokenService(
    val refreshTokenRepository: RefreshTokenRepository
) {
    fun deleteByAccessTokenId(id: String) {
        val refreshToken = refreshTokenRepository.findByAccessTokenId(id)
        refreshToken?.let{
            refreshTokenRepository.deleteById(it.id)
        }
    }

    fun renewingToken(id: String, newTokenId: String): RefreshToken? {
        val refreshToken = findByAccessTokenId(id)

        refreshToken ?: return null
        refreshToken.token = UUID.randomUUID().toString()
        refreshToken.accessTokenId = newTokenId
        refreshTokenRepository.save<RefreshToken>(refreshToken)
        return refreshToken
    }

    fun findByAccessTokenId(id: String): RefreshToken? {
        return refreshTokenRepository.findByAccessTokenId(id)
    }
}
