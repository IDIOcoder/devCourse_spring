package com.grepp.spring.app.controller.api.auth.payload;

import com.grepp.spring.infra.auth.token.GrantType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private String accessToken;
    private GrantType grantType;
    private Long expiresIn;
}
