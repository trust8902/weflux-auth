package com.firststory.auth.dto

data class ClientRegisterDtoRequest(
    val clientId: String,
    val clientSecret: String,
    val clientName: String,
    val redirectUris: Set<String>,
    val scopes: Set<String>,
    val authorizationGrantTypes: Set<String>,
    val clientAuthenticationMethods: Set<String>,
    val accessTokenValiditySeconds: Int,
    val refreshTokenValiditySeconds: Int,
)
