package com.grepp.spring.app.model.auth;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.auth.dto.TokenDto;
import com.grepp.spring.app.model.auth.entity.RefreshToken;
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
public class AuthService implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    
    public TokenDto signin(String username, String password){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        // loadUserByUsername + password 검증 후 authentication 반환
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String accessToken = jwtProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = new RefreshToken(authentication.getName());
        refreshTokenRepository.save(refreshToken);
        
        return TokenDto.builder()
                   .accessToken(accessToken)
                   .refreshToken(refreshToken.getToken())
                   .atExpiresIn(jwtProvider.getAtExpiration())
                   .rtExpiresIn(jwtProvider.getRtExpiration())
                   .grantType(GrantType.BEARER)
                   .build();
    }
    
    
    @Override
    public UserDetails loadUserByUsername(String username){
        
        Member member = memberRepository.findById(username)
                            .orElseThrow(() -> new UsernameNotFoundException(username));
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));
        
        List<TeamMember> teamMembers = teamMemberRepository.findByUserIdAndActivated(username, true);
        
        // 스프링시큐리티는 기본적으로 권한 앞에 ROLE_ 이 있음을 가정
        // hasRole("ADMIN") =>  ROLE_ADMIN 권한이 있는 지 확인.
        // TEAM_{teamId}:{role}
        // hasAuthority("ADMIN") => ADMIN 권한을 확인
        List<SimpleGrantedAuthority> teamAuthorities =
            teamMembers.stream().map(e -> new SimpleGrantedAuthority("TEAM_" + e.getTeamId() + ":" + e.getRole()))
                .toList();
        
        authorities.addAll(teamAuthorities);
        return Principal.createPrincipal(member, authorities);
    }
    
    
}
