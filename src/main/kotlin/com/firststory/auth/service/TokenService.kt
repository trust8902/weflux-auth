package com.firststory.auth.service

import io.jsonwebtoken.Jwts
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Service
class TokenService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val authTokenSecret: SecretKey = Jwts.SIG.HS256.key().build()
    private val accessTokenSecret: SecretKey = Jwts.SIG.HS256.key().build()
    private val refreshTokenSecret: SecretKey = Jwts.SIG.HS256.key().build()

    fun generateAuthToken(): String {
        return Jwts.builder()
            .subject("auth")
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(AUTH_TOKEN_EXPIRATION)))
            .signWith(authTokenSecret)
            .compact()
    }

    fun generateAccessToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(ACCESS_TOKEN_EXPIRATION)))
            .signWith(accessTokenSecret)
            .compact()
    }

    fun generateRefreshToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION)))
            .signWith(refreshTokenSecret)
            .compact()
    }

    fun saveAccessToken(username: String, token: String): Mono<Boolean> {
        return redisTemplate
            .opsForValue()
            .set("access:$username", token, Duration.ofMillis(ACCESS_TOKEN_EXPIRATION))
    }

    fun saveRefreshToken(username: String, token: String): Mono<Boolean> {
        return redisTemplate
            .opsForValue()
            .set("refresh:$username", token, Duration.ofMillis(REFRESH_TOKEN_EXPIRATION))
    }

    fun getAccessToken(username: String): Mono<String> {
        return redisTemplate.opsForValue().get("access:$username")
    }

    fun getRefreshToken(username: String): Mono<String> {
        return redisTemplate.opsForValue().get("refresh:$username")
    }

    fun verifyRefreshToken(refreshToken: String): String {
        return Jwts.parser()
            .verifyWith(refreshTokenSecret as SecretKey)
            .build()
            .parseSignedClaims(refreshToken)
            .payload
            .subject
    }

    companion object {
        private const val AUTH_TOKEN_EXPIRATION = 30L * 24 * 60 * 60 * 1000 // 30 days
        private const val ACCESS_TOKEN_EXPIRATION = 15L * 60 * 1000 // 15 minutes
        private const val REFRESH_TOKEN_EXPIRATION = 7L * 24 * 60 * 60 * 1000 // 7 days
    }

}