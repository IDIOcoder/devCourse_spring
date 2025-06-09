package com.grepp.spring.token.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.util.*

@RedisHash(value = "refreshToken", timeToLive = 3600 * 24 * 7)
class RefreshToken(
    @Id
    val id:String = UUID.randomUUID().toString(),
    var email: String,
    @Indexed
    var accessTokenId: String,
    var token: String = UUID.randomUUID().toString()
) {

}
