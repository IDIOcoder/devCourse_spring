package com.grepp.gateway.security.oauth2

import com.grepp.gateway.security.AuthService
import com.grepp.gateway.security.oauth2.user.OAuth2UserInfo
import com.grepp.gateway.security.token.JwtProvider
import com.grepp.gateway.security.token.TokenCookieFactory
import com.grepp.gateway.security.token.TokenResponseExecutor
import com.grepp.gateway.security.token.code.TokenType
import com.grepp.gateway.security.token.dto.TokenDto
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class OAuth2SuccessHandler(
    private val authService: AuthService
) : SimpleUrlAuthenticationSuccessHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val user: OAuth2User = authentication.principal as OAuth2User
        val userInfo: OAuth2UserInfo = OAuth2UserInfo.createUserInfo(request.requestURI, user)
        val tokenDto: TokenDto = authService.processTokenSignin(userInfo.name)
        val res = TokenResponseExecutor.response(response, tokenDto)
        redirectStrategy.sendRedirect(request, res, "/")
    }
}
