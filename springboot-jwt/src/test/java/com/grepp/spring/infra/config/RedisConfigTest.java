package com.grepp.spring.infra.config;

import static org.springframework.cloud.client.discovery.ReactiveDiscoveryClient.LOG;

import com.grepp.spring.app.model.auth.RefreshTokenRepository;
import com.grepp.spring.app.model.auth.entity.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootTest
class RedisConfigTest {
    
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    
    @Test
    public void testRedisTemplate(){
        refreshTokenRepository.save(new RefreshToken("super@grepp.com"));
    }
}