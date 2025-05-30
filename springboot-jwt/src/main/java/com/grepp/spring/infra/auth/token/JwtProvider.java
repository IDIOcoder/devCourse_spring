package com.grepp.spring.infra.auth.token;

import com.grepp.spring.app.model.auth.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    
    private RefreshTokenRepository refreshTokenRepository;
    
    @Value("${jwt.secrete}")
    private String key;
    
    @Value("${jwt.access-expiration}")
    private long atExpiration;
    
    @Value("${jwt.refresh-expiration}")
    private long rtExpiration;
    
    private final SecretKey secretKey;
    
    public JwtProvider() {
        String base64Key = Base64.getEncoder().encodeToString(key.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(base64Key.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateAccessToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                                 .map(GrantedAuthority::getAuthority)
                                 .collect(Collectors.joining(","));
        
        long now = new Date().getTime();
        Date atExpiresIn = new Date(now + atExpiration);
        return Jwts.builder()
                   .subject(authentication.getName())
                   .claim("auth",authorities)
                   .expiration(atExpiresIn)
                   .signWith(this.secretKey)
                   .compact();
    }
    
}
