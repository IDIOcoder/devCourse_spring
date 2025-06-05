package com.grepp.spring.infra.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.grepp.spring.infra.auth.oauth2.OAuth2FailureHandler;
import com.grepp.spring.infra.auth.oauth2.OAuth2SuccessHandler;
import com.grepp.spring.infra.auth.token.filter.AuthExceptionFilter;
import com.grepp.spring.infra.auth.token.filter.LogoutFilter;
import com.grepp.spring.infra.auth.token.JwtAuthenticationEntryPoint;
import com.grepp.spring.infra.auth.token.filter.JwtAuthenticationFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthExceptionFilter authExceptionFilter;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final LogoutFilter logoutFilter;
    
    @Bean
    public AuthenticationSuccessHandler successHandler(){
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                HttpServletResponse response, Authentication authentication)
                throws IOException, ServletException {
                
                boolean isAdmin = authentication.getAuthorities()
                                      .stream()
                                      .anyMatch(authority ->
                                                    authority.getAuthority().equals("ROLE_ADMIN"));
                
                if(isAdmin){
                    response.sendRedirect("/admin");
                    return;
                }
                
                response.sendRedirect("/");
            }
        };
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        // * : 1depth 아래 모든 경로
        // ** : 모든 depth 의 모든 경로
        // Security Config 에는 인증과 관련된 설정만 지정 (PermitAll or Authenticated)
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .oauth2Login(oauth ->
                             oauth.successHandler(oAuth2SuccessHandler)
                                 .failureHandler(oAuth2FailureHandler)
                            
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(
                (requests) -> requests
                                  .requestMatchers(GET, "/", "/error", "/favicon.ico", "/css/**", "/img/**","/js/**","/download/**").permitAll()
                                  .requestMatchers(GET, "/book/list").permitAll()
                                  .requestMatchers(GET, "/api/book/list", "/api/member/exists/*", "/api/ai/**").permitAll()
                                  .requestMatchers(GET, "/member/signup", "/member/signup/*", "/member/signin").permitAll()
                                  .requestMatchers(POST, "/member/signin", "/member/signup", "/member/verify").permitAll()
                                  .requestMatchers(POST, "/auth/signin").permitAll()
                                  .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(logoutFilter, JwtAuthenticationFilter.class)
            .addFilterBefore(authExceptionFilter, JwtAuthenticationFilter.class);
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOriginPatterns(Collections.singletonList(
            "http://localhost:8081"
        ));
        
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST"));
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
        corsConfig.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}
