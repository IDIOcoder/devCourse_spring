package com.grepp.gateway.infra.config

import com.grepp.gateway.filter.JwtAuthenticationFilter
import com.grepp.gateway.filter.LogoutFilter
import com.grepp.gateway.security.oauth2.OAuth2SuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val logoutFilter: LogoutFilter,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf(Customizer<CsrfConfigurer<HttpSecurity>> { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() })
            .formLogin(Customizer<FormLoginConfigurer<HttpSecurity>> { obj: FormLoginConfigurer<HttpSecurity> -> obj.disable() })
            .httpBasic(Customizer<HttpBasicConfigurer<HttpSecurity>> { obj: HttpBasicConfigurer<HttpSecurity> -> obj.disable() })
            .sessionManagement(Customizer<SessionManagementConfigurer<HttpSecurity>> { session: SessionManagementConfigurer<HttpSecurity> ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            })
            .oauth2Login { it.successHandler(oAuth2SuccessHandler) }
            .authorizeHttpRequests(
                Customizer { it.anyRequest().authenticated() }
            )
            .addFilterBefore(logoutFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

}
