package com.firststory.auth.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

@Service
class TokenService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    companion object {
        private const val AUTH_TOKEN_EXPIRATION_DAYS = 30L * 24 * 60 * 60 * 1000 // 30 days
        private const val ACCESS_TOKEN_EXPIRATION_HOURS = 15L * 60 * 1000 // 15 minutes
        private const val REFRESH_TOKEN_EXPIRATION_DAYS = 7L * 24 * 60 * 60 * 1000 // 7 days

        private val AUTH_TOKEN_SECRET: SecretKey = Jwts.SIG.HS256.key().build()
        private val ACCESS_TOKEN_SECRET: SecretKey = Jwts.SIG.HS256.key().build()
        private val REFRESH_TOKEN_SECRET: SecretKey = Jwts.SIG.HS256.key().build()
    }


    fun createAuthToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + AUTH_TOKEN_EXPIRATION_DAYS * 86400000))
            .signWith(AUTH_TOKEN_SECRET)
            .compact()
    }

    fun createAccessToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_HOURS * 3600000))
            .signWith(ACCESS_TOKEN_SECRET)
            .compact()
    }

    fun createRefreshToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_DAYS * 86400000))
            .signWith(REFRESH_TOKEN_SECRET)
            .compact()
    }

    fun saveAccessToken(userId: Long, token: String): Mono<Boolean> {
        return redisTemplate
            .opsForValue()
            .set("accessToken:$userId", token, Duration.ofHours(ACCESS_TOKEN_EXPIRATION_HOURS))
    }

    fun saveRefreshToken(userId: Long, token: String): Mono<Boolean> {
        return redisTemplate
            .opsForValue()
            .set("refreshToken:$userId", token, Duration.ofDays(REFRESH_TOKEN_EXPIRATION_DAYS))
    }

    fun getAccessToken(userId: Long): Mono<String> {
        return redisTemplate.opsForValue().get("accessToken:$userId").map { it }
    }

    fun getRefreshToken(userId: Long): Mono<String> {
        return redisTemplate.opsForValue().get("refreshToken:$userId").map { it }
    }

    fun verifyRefreshToken(token: String): Mono<Jws<Claims>> {
        return Mono.fromCallable {
            Jwts.parser().verifyWith(REFRESH_TOKEN_SECRET).build().parseSignedClaims(token)
        }
    }

    fun getUserIdByRefreshToken(userId: Long): Mono<String> {
        return redisTemplate.opsForValue().get("refreshToken:$userId").map { it }
    }

}