package com.grepp.gateway.security.token.dto

class AccessTokenDto(
    val id: String,
    val token: String,
    val expiresIn: Long,
) {

}
