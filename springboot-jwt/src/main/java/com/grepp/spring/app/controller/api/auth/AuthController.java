package com.grepp.spring.app.controller.api.auth;

import com.grepp.spring.app.controller.api.auth.payload.SigninRequest;
import com.grepp.spring.app.controller.api.auth.payload.TokenResponse;
import com.grepp.spring.app.model.auth.AuthService;
import com.grepp.spring.app.model.auth.token.dto.TokenDto;
import com.grepp.spring.infra.auth.token.GrantType;
import com.grepp.spring.infra.auth.token.TokenCookieFactory;
import com.grepp.spring.infra.auth.token.TokenType;
import com.grepp.spring.infra.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("signin")
    public ResponseEntity<ApiResponse<TokenResponse>> signin(
        @RequestBody
        SigninRequest req,
        HttpServletResponse response
    ) {
        TokenDto dto = authService.signin(req.getUsername(), req.getPassword());
        ResponseCookie accessTokenCookie = TokenCookieFactory.create(TokenType.ACCESS_TOKEN.name(),
            dto.getAccessToken(), dto.getAtExpiresIn());
        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(TokenType.REFRESH_TOKEN.name(),
            dto.getRefreshToken(), dto.getRtExpiresIn());
        
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        TokenResponse tokenResponse = TokenResponse.builder()
                                          .accessToken(dto.getAccessToken())
                                          .expiresIn(dto.getAtExpiresIn())
                                          .grantType(GrantType.BEARER)
                                          .build();
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));
    }
    
    @PostMapping("logout")
    public ResponseEntity<ApiResponse<Void>> logout(
        HttpServletResponse response
    ){
        ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(TokenType.ACCESS_TOKEN);
        ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(TokenType.REFRESH_TOKEN);
        ResponseCookie expiredAuthSessionId = TokenCookieFactory.createExpiredToken(TokenType.AUTH_SERVER_SESSION_ID);
        response.addHeader("Set-Cookie", expiredAccessToken.toString());
        response.addHeader("Set-Cookie", expiredRefreshToken.toString());
        response.addHeader("Set-Cookie", expiredAuthSessionId.toString());
        return ResponseEntity.ok(ApiResponse.noContent());
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
