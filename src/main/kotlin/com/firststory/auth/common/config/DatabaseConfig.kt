package com.firststory.auth.common.config

import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
@ConfigurationProperties(prefix = "spring.r2dbc")
class DatabaseConfig {

    lateinit var url: String
    lateinit var username: String
    lateinit var password: String

    @Bean
    fun connectionFactory(): ConnectionFactory {
        return PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(extractHost(url))
                .port(extractPort(url))
                .database(extractDatabase(url))
                .username(username)
                .password(password)
                .build()
        )
    }

    private fun extractHost(ur: String): String {
        return url.substringAfter("://").substringBefore(":")
    }

    private fun extractPort(url: String): Int {
        return url.substringAfter(":").substringBefore("/").toInt()
    }

    private fun extractDatabase(url: String): String {
        return url.substringAfterLast("/")
    }

}