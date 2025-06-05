package com.grepp.gateway.security.token.dto

import com.grepp.gateway.security.token.code.GrantType

class TokenDto(
    val accessToken: String,
    val atId:String,
    val refreshToken: String,
    val grantType: GrantType,
    val atExpiresIn: Long,
    val rtExpiresIn: Long
) {

}

