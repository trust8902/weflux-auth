package com.firststory.auth.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository

@Configuration
class AuthorizationServerConfig {

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        RegisteredClient.withId("client-1")
    }

}