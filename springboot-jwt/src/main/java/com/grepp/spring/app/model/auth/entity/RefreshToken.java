package com.grepp.spring.app.model.auth.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter @Setter
@RedisHash(value = "refreshToken", timeToLive = 3600 * 24 * 7)
public class RefreshToken {
    @Id
    private String email;
    private String token = UUID.randomUUID().toString();
    
    public RefreshToken(String email) {
        this.email = email;
    }
}
