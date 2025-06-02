package com.grepp.spring.infra.config;

import com.grepp.spring.app.model.auth.token.RefreshTokenRepository;
import com.grepp.spring.app.model.auth.token.entity.RefreshToken;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisConfigTest {
    
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    
    @Test
    public void testRedisTemplate(){
        refreshTokenRepository.save(new RefreshToken("super@grepp.com", "test-uuid"));
    }
    
    @Test
    public void testDelete(){
        Optional<RefreshToken> optional = refreshTokenRepository.findByAccessTokenId("2929f7d5-6914-497b-bc6f-8cb81b195881");
        optional.ifPresent(e -> refreshTokenRepository.deleteById(e.getId()));
    }
    

}