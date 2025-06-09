package com.grepp.spring.infra.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories
class RedisConfig {
    @Value("\${spring.data.redis.port}")
    private var port = 0

    @Value("\${spring.data.redis.host}")
    private var host: String = ""

    @Value("\${spring.data.redis.username}")
    private var username: String = ""

    @Value("\${spring.data.redis.password}")
    private var password: String = ""

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val configuration: RedisStandaloneConfiguration = RedisStandaloneConfiguration()
        configuration.username = username
        configuration.port = port
        configuration.hostName = host
        configuration.setPassword(password)
        return LettuceConnectionFactory(configuration)
    }
}
