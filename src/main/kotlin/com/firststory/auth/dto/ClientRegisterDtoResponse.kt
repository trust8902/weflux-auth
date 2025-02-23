package com.firststory.auth.dto

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

data class ClientRegisterDtoResponse(
    val clientId: String,
    val clientSecret: String,
    val clientName: String,
    val redirectUris: Set<String>,
    val scopes: Set<String>,
    val authorizationGrantTypes: Set<String>,
    val clientAuthenticationMethods: Set<String>?,
    val accessTokenValiditySeconds: Int,
    val refreshTokenValiditySeconds: Int,
) {
    companion object {
        fun fromEntity(client: RegisteredClient): Mono<ClientRegisterDtoResponse> {
            return Mono.just(
                ClientRegisterDtoResponse(
                    clientId = client.clientId,
                    clientSecret = client.clientSecret!!,
                    clientName = client.clientName,
                    redirectUris = client.redirectUris,
                    scopes = client.scopes,
                    authorizationGrantTypes = client.authorizationGrantTypes.map { it.value }.toSet(),
                    clientAuthenticationMethods = client.clientAuthenticationMethods.map { it.value }.toSet(),
                    accessTokenValiditySeconds = client.tokenSettings.accessTokenTimeToLive.toMinutes().toInt(),
                    refreshTokenValiditySeconds = client.tokenSettings.refreshTokenTimeToLive.toMinutes().toInt()
                )
            )
        }
    }
}
