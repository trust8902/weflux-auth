package com.firststory.auth.repository

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import reactor.core.publisher.Mono

interface ReactiveRegisteredClientRepository {
    fun save(registeredClient: RegisteredClient): Mono<Void>  // 비동기 저장
    fun findByClientId(clientId: String): Mono<RegisteredClient>  // 비동기 조회
    fun findById(id: String): Mono<RegisteredClient>  // 비동기 조회
}