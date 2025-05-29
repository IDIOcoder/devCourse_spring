package com.grepp.spring.infra.config;

import com.grepp.spring.app.model.auth.RefreshTokenRepository;
import com.grepp.spring.app.model.auth.entity.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisConfigTest {
    
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    
    @Test
    public void testRedisTemplate(){
        refreshTokenRepository.save(new RefreshToken("super@grepp.com"));
    }
}