package com.grepp.gateway.security.token

import com.grepp.gateway.infra.error.CommonException
import com.grepp.gateway.security.token.code.TokenType
import com.grepp.gateway.security.token.dto.AccessTokenDto
import com.grepp.gateway.security.token.repository.RefreshTokenRepository
import com.grepp.greppauth.infra.response.ResponseCode
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtProvider(private val refreshTokenRepository: RefreshTokenRepository) {

    val log: Logger = LoggerFactory.getLogger(javaClass)

    @Value("\${jwt.secrete}")
    val key: String? = null

    @Value("\${jwt.access-expiration}")
    val atExpiration: Long = 0

    @Value("\${jwt.refresh-expiration}")
    val rtExpiration: Long = 0

    private final var secretKey: SecretKey? = null
        get() {
            if (field == null) {
                val base64Key = Base64.getEncoder().encodeToString(key?.toByteArray())
                field = Keys.hmacShaKeyFor(base64Key.toByteArray(StandardCharsets.UTF_8))
            }
            return field
        }

    fun generateAccessToken(username: String?): AccessTokenDto {
        val id = UUID.randomUUID().toString()
        val now = Date().time
        val atExpiresIn = Date(now + atExpiration)
        val accessToken: String = Jwts.builder()
            .subject(username)
            .id(id)
            .expiration(atExpiresIn)
            .signWith(secretKey)
            .compact()

        return AccessTokenDto(
            id=id,
            token = accessToken,
            expiresIn = atExpiration,
        )
    }

    fun parseClaim(accessToken: String?): Claims {
        return try {
            Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(accessToken).payload
        } catch (ex: ExpiredJwtException) {
            ex.claims
        } catch (ex: Exception){
            throw CommonException(ResponseCode.BAD_REQUEST)
        }
    }

    fun validateToken(requestAccessToken: String?): Boolean {
        try {
            Jwts.parser().verifyWith(secretKey).build().parse(requestAccessToken)
            return true
        } catch (e: Exception){
            if(e is ExpiredJwtException){
                throw e
            }
            return false
        }
    }

    fun resolveToken(request: HttpServletRequest, tokenType: TokenType): String? {
        val headerToken = request.getHeader("Authorization")
        if (headerToken != null && headerToken.startsWith("Bearer")) {
            return headerToken.substring(7)
        }

        val cookies = request.cookies ?: return null
        return Arrays.stream(cookies)
            .filter { e: Cookie -> e.name == tokenType.name }
            .map { obj: Cookie -> obj.value }.findFirst()
            .orElse(null)
    }
}
