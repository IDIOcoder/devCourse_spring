package com.grepp.spring.app.model.auth.token.dto;

import com.grepp.spring.infra.auth.token.GrantType;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
@Builder
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private GrantType grantType;
    private Long atExpiresIn;
    private Long rtExpiresIn;
}

