package com.grepp.spring.infra.auth.token;

import org.springframework.http.ResponseCookie;

public class TokenCookieFactory {
    public static ResponseCookie create(String name, String value, Long expires){
        return ResponseCookie.from(name, value)
                   .httpOnly(true)
                   .maxAge(expires/1000)
                   // todo : 배포 후 true 로 변경
                   .secure(false)
                   .path("/")
                   .build();
    }
}
