package com.firststory.auth.service

import com.firststory.auth.dto.ClientRegisterDtoRequest
import com.firststory.auth.dto.ClientRegisterDtoResponse
import com.firststory.auth.repository.RedisRegisteredClientRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*

@Service
class ClientService(
    private val redisRegisteredClientRepository: RedisRegisteredClientRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun register(dto: ClientRegisterDtoRequest): Mono<ClientRegisterDtoResponse> {
        val encodedSecret = passwordEncoder.encode(dto.clientSecret)
        val client = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(dto.clientId)
            .clientSecret("{bcrypt}$encodedSecret")
            .clientName(dto.clientName)
            .apply {
                dto.redirectUris.forEach { redirectUri(it) }
                dto.scopes.forEach { scope(it) }
                dto.authorizationGrantTypes.forEach { authorizationGrantType(AuthorizationGrantType(it)) }
                dto.clientAuthenticationMethods.forEach { clientAuthenticationMethod(ClientAuthenticationMethod(it)) }
            }
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(dto.accessTokenValiditySeconds.toLong())) // 여기서 Duration을 사용하고 있음
                    .refreshTokenTimeToLive(Duration.ofDays(dto.refreshTokenValiditySeconds.toLong()))  // Duration 타입
                    .build()
            )
            .build()

        // redisRegisteredClientRepository.save(client)가 Mono<Void>를 반환하도록 수정
        return redisRegisteredClientRepository.save(client)
            .then(ClientRegisterDtoResponse.fromEntity(client))
    }

//    private fun convertGrandType(grantType: String): AuthorizationGrantType {
//        return when (grantType) {
//            "authorization_code" -> AuthorizationGrantType.AUTHORIZATION_CODE
//            "refresh_token" -> AuthorizationGrantType.REFRESH_TOKEN
//            "client_credentials" -> AuthorizationGrantType.CLIENT_CREDENTIALS
//            else -> AuthorizationGrantType(grantType) // 사용자 정의 grant type
//        }
//    }
}