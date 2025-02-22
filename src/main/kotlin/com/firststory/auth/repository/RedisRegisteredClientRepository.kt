package com.firststory.auth.repository

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class RedisRegisteredClientRepository(
    private val redisTemplate: ReactiveRedisTemplate<String, RegisteredClient>
) : ReactiveRegisteredClientRepository {

    private fun getKey(clientId: String) = "client:$clientId"

    // 비동기적으로 클라이언트를 Redis에 저장
    override fun save(registeredClient: RegisteredClient): Mono<Void> {
        return redisTemplate.opsForValue()
            .set(getKey(registeredClient.clientId), registeredClient) // 비동기적으로 클라이언트 저장
            .then()  // Mono<Void>를 반환
    }

    // 비동기적으로 clientId로 RegisteredClient 조회
    override fun findByClientId(clientId: String): Mono<RegisteredClient> {
        return redisTemplate.opsForValue()
            .get(getKey(clientId))  // 비동기적으로 Redis에서 클라이언트 조회
            .switchIfEmpty(Mono.empty())  // 값이 없을 경우 빈 Mono 반환
    }

    // 비동기적으로 ID로 RegisteredClient 조회
    override fun findById(id: String): Mono<RegisteredClient> {
        return redisTemplate.keys("client:*")
            .flatMap { key -> redisTemplate.opsForValue().get(key) }
            .filter { it.id == id }
            .next()  // 첫 번째 결과 반환
            .switchIfEmpty(Mono.empty())  // 값이 없을 경우 빈 Mono 반환
    }
}


