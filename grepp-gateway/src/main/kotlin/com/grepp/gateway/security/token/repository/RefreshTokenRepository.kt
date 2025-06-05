package com.grepp.gateway.security.token.repository

import com.grepp.gateway.security.token.entity.RefreshToken
import org.springframework.data.repository.CrudRepository
import java.util.*

interface RefreshTokenRepository : CrudRepository<RefreshToken, String> {
    fun findByAccessTokenId(id: String): RefreshToken?
    fun deleteByToken(token: String)
}
