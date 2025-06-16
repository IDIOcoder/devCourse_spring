package com.grepp.spring.app.model.auth;

import com.grepp.spring.app.controller.api.auth.payload.SigninRequest;
import com.grepp.spring.app.model.auth.token.RefreshTokenService;
import com.grepp.spring.app.model.auth.token.UserBlackListRepository;
import com.grepp.spring.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.spring.app.model.auth.token.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.infra.auth.token.JwtProvider;
import com.grepp.spring.infra.auth.token.code.GrantType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService{
    
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;
    
    public TokenDto signin(SigninRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword());
        
        // loadUserByUsername + password 검증 후 인증 객체 반환
        // 인증 실패 시: AuthenticationException 발생
        Authentication authentication = authenticationManagerBuilder.getObject()
                                            .authenticate(authenticationToken);
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return processTokenSignin(authentication.getName());
    }
    
    public TokenDto processTokenSignin(String email){
        // black list 에 있다면 해제
        userBlackListRepository.deleteById(email);
        
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        AccessTokenDto accessToken = jwtProvider.generateAccessToken(email);
        RefreshToken refreshToken = refreshTokenService.saveWithAtId(accessToken.getId());
        
        return TokenDto.builder()
                   .accessToken(accessToken.getToken())
                   .refreshToken(refreshToken.getToken())
                   .grantType(GrantType.BEARER)
                   .atExpiresIn(jwtProvider.getAtExpiration())
                   .rtExpiresIn(jwtProvider.getRtExpiration())
                   .build();
    }
    
}
