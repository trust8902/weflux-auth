package com.firststory.auth.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient

@Configuration
class RedisConfig {
    @Bean
    fun customRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val config = RedisStandaloneConfiguration("158.247.250.91", 6379)
        return LettuceConnectionFactory(config)
    }

    @Bean
    @Primary
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {
        return ReactiveRedisTemplate(factory, RedisSerializationContext.string())
    }

    @Bean
    fun registeredClientReactiveRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, RegisteredClient> {

        val keySerializer = StringRedisSerializer()

        // ObjectMapper를 직접 설정
        val objectMapper = Jackson2ObjectMapperBuilder.json()
            .modulesToInstall(JavaTimeModule())  // Java 8 Date/Time 처리
            .build<ObjectMapper>()

        // Jackson2JsonRedisSerializer에 ObjectMapper를 설정하는 대신 직접 직렬화/역직렬화 처리
        val valueSerializer = Jackson2JsonRedisSerializer(RegisteredClient::class.java)
        val valueSerializationContext = RedisSerializationContext
            .newSerializationContext<String, RegisteredClient>(keySerializer)
            .value(valueSerializer)
            .build()

        return ReactiveRedisTemplate(connectionFactory, valueSerializationContext)
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Any> {
        val objectMapper = Jackson2ObjectMapperBuilder.json()
            .modulesToInstall(JavaTimeModule())  // Java 8 Date/Time 처리
            .build<ObjectMapper>()

        // Jackson2JsonRedisSerializer에 ObjectMapper 설정
        val serializer = Jackson2JsonRedisSerializer(Any::class.java)
        val valueSerializationContext = RedisSerializationContext
            .newSerializationContext<String, Any>(StringRedisSerializer())
            .value(serializer)
            .build()

        return ReactiveRedisTemplate(redisConnectionFactory, valueSerializationContext)
    }
}