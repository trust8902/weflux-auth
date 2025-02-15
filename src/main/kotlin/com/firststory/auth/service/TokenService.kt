package com.firststory.auth.service

import io.jsonwebtoken.Jwts
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.security.Key
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class TokenService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val authTokenSecret: Key = Jwts.SIG.HS256.key().build()
    private val accessTokenSecret: Key = Jwts.SIG.HS256.key().build()

    fun generateAuthToken(): String {
        return Jwts.builder()
            .subject("auth")
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(AUTH_TOKEN_EXPIRATION)))
            .signWith(authTokenSecret)
            .compact()
    }

    fun generateAccessToken(userName: String): String {
        return Jwts.builder()
            .subject(userName)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(ACCESS_TOKEN_EXPIRATION)))
            .signWith(accessTokenSecret)
            .compact()
    }

    fun saveAccessToken(userName: String, accessToken: String): Mono<Boolean> {
        return redisTemplate.opsForValue().set(userName, accessToken, Duration.ofHours(1))
    }

    fun getAccessToken(userName: String): Mono<String> {
        return redisTemplate.opsForValue().get(userName)
    }

    companion object {
        private const val AUTH_TOKEN_EXPIRATION = 30L * 24 * 60 * 60 * 1000 // 30 days
        private const val ACCESS_TOKEN_EXPIRATION = 15L * 60 * 1000 // 15 minutes
    }

}