package com.grepp.spring.infra.auth.token;

import com.grepp.spring.app.model.auth.token.RefreshTokenRepository;
import com.grepp.spring.app.model.auth.token.UserBlackListRepository;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.auth.token.entity.UserBlackList;
import com.grepp.spring.infra.error.exceptions.CommonException;
import com.grepp.spring.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserBlackListRepository userBlackListRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        
        String requestAccessToken = resolveToken(request, TokenType.ACCESS_TOKEN);
        if (requestAccessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        
        Claims claims = jwtProvider.parseClaim(requestAccessToken);

        if(request.getRequestURI().equals("/auth/logout")){
            Optional<RefreshToken> optional = refreshTokenRepository.findByAccessTokenId(claims.getId());
            optional.ifPresent(refreshTokenRepository::delete);
            filterChain.doFilter(request, response);
            return;
        }
        
        if(userBlackListRepository.existsById(claims.getSubject())){
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            if(jwtProvider.validateToken(requestAccessToken)){
                Authentication authentication = jwtProvider.genreateAuthentication(requestAccessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException ex) {
            String newAccessToken = renewingAccessToken(requestAccessToken, request);
            if (newAccessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }
            RefreshToken newRefreshToken = renewingRefreshToken(claims.getId());
            responseToken(response, newAccessToken, newRefreshToken);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private void responseToken(HttpServletResponse response, String newAccessToken,
        RefreshToken newRefreshToken) {
        
        ResponseCookie accessTokenCookie =
            TokenCookieFactory.create(TokenType.ACCESS_TOKEN.name(), newAccessToken,
                3000000L);
        
        ResponseCookie refreshTokenCookie =
            TokenCookieFactory.create(TokenType.REFRESH_TOKEN.name(), newRefreshToken.getToken(),
                jwtProvider.getAtExpiration());
        
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
    
    private RefreshToken renewingRefreshToken(String id) {
        RefreshToken refreshToken = refreshTokenRepository.findByAccessTokenId(id).get();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
    
    private String renewingAccessToken(String requestAccessToken, HttpServletRequest request) {
        Authentication authentication = jwtProvider.genreateAuthentication(requestAccessToken);
        String refreshToken = resolveToken(request, TokenType.REFRESH_TOKEN);
        Claims claims = jwtProvider.parseClaim(requestAccessToken);
        Optional<RefreshToken> optional = refreshTokenRepository.findByAccessTokenId(claims.getId());
        if (optional.isEmpty()) {
            return null;
        }
        
        RefreshToken storedRefreshToken = optional.get();
        if (!storedRefreshToken.getToken().equals(refreshToken)) {
            userBlackListRepository.save(new UserBlackList(authentication.getName()));
            throw new CommonException(ResponseCode.SECURITY_INCIDENT);
        }
        
        return generateAccessToken(authentication);
    }
    
    private String generateAccessToken(Authentication authentication) {
        String newAccessToken = jwtProvider.generateAccessToken(authentication).getToken();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return newAccessToken;
    }
    
    private String resolveToken(HttpServletRequest request, TokenType tokenType) {
        String headerToken = request.getHeader("Authorization");
        if (headerToken != null && headerToken.startsWith("Bearer")) {
            return headerToken.substring(7);
        }
        
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        
        return Arrays.stream(cookies)
                   .filter(e -> e.getName().equals(tokenType.name()))
                   .map(Cookie::getValue).findFirst()
                   .orElse(null);
        
    }
}
