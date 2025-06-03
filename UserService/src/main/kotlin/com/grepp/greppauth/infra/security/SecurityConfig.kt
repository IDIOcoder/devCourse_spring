package com.grepp.greppauth.infra.security.domain

import com.grepp.greppauth.infra.auth.jwt.JwtAuthenticationFilter
import com.grepp.greppauth.infra.auth.jwt.JwtExceptionFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val jwtExceptionFilter: JwtExceptionFilter
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf(Customizer<CsrfConfigurer<HttpSecurity?>> { obj: CsrfConfigurer<HttpSecurity?> -> obj.disable() })
            .formLogin(Customizer<FormLoginConfigurer<HttpSecurity?>> { obj: FormLoginConfigurer<HttpSecurity?> -> obj.disable() })
            .httpBasic(Customizer<HttpBasicConfigurer<HttpSecurity?>> { obj: HttpBasicConfigurer<HttpSecurity?> -> obj.disable() })
            .sessionManagement(Customizer<SessionManagementConfigurer<HttpSecurity?>> { session: SessionManagementConfigurer<HttpSecurity?> ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            })
            .authorizeHttpRequests(
                Customizer { it.requestMatchers(
                            HttpMethod.GET,
                            "/",
                            "/error",
                            "/favicon.ico",
                            "/img/**",
                            "/js/**",
                            "/css/**",
                            "/download/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/book/list").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/book/list").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/member/exists/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/member/signup").permitAll()
                        .requestMatchers(HttpMethod.GET, "/member/signin").permitAll()
                        .requestMatchers(HttpMethod.POST, "/member/signin", "/member/signup")
                        .permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                }
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter::class.java)
        return http.build()
    }

}
