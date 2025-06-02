package com.grepp.spring.app.model.auth;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.auth.token.RefreshTokenRepository;
import com.grepp.spring.app.model.auth.token.UserBlackListRepository;
import com.grepp.spring.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.spring.app.model.auth.token.dto.TokenDto;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import com.grepp.spring.app.model.member.MemberRepository;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.team.TeamMemberRepository;
import com.grepp.spring.app.model.team.entity.TeamMember;
import com.grepp.spring.infra.auth.token.GrantType;
import com.grepp.spring.infra.auth.token.JwtProvider;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserBlackListRepository userBlackListRepository;
    private final JwtProvider jwtProvider;
    
    public TokenDto signin(String username, String password){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        // loadUserByUsername + password 검증 후 authentication 반환
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 블랙리스트에서 제거
        userBlackListRepository.deleteById(username);
        
        AccessTokenDto dto = jwtProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = new RefreshToken(authentication.getName(), dto.getId());
        refreshTokenRepository.save(refreshToken);
        
        return TokenDto.builder()
                   .accessToken(dto.getToken())
                   .refreshToken(refreshToken.getToken())
                   .atExpiresIn(jwtProvider.getAtExpiration())
                   .rtExpiresIn(jwtProvider.getRtExpiration())
                   .grantType(GrantType.BEARER)
                   .build();
    }
}
