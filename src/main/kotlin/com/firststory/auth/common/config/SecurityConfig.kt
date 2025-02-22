package com.firststory.auth.common.config

import com.firststory.auth.repository.ReactiveRegisteredClientRepository
import com.firststory.auth.repository.RedisRegisteredClientRepository
import com.firststory.auth.service.MemberService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val memberService: MemberService,
    private val passwordEncoder: PasswordEncoder
) {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.authorizeExchange {
            it.pathMatchers("/signup", "/login", "/oauth2/**").permitAll()
                .anyExchange().authenticated()
        }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .oauth2ResourceServer {
                it.jwt { jwtConfigurer ->
                    jwtConfigurer.jwtAuthenticationConverter(ReactiveJwtAuthenticationConverterAdapter(JwtAuthenticationConverter()))
                }
            }
            .build()
    }

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager {
        return UserDetailsRepositoryReactiveAuthenticationManager(memberService as ReactiveUserDetailsService).apply {
            setPasswordEncoder(passwordEncoder)
        }
    }

    @Bean
    fun reactiveJwtDecoder(): ReactiveJwtDecoder {
        return NimbusReactiveJwtDecoder.withSecretKey(SecretKeySpec("wJ6f9x7s8G2b5Q0n3H1p8L4m6R2d0F3y".toByteArray(), "HmacSHA256")).build()
    }

    @Bean
    fun registeredClientRepository(redisTemplate: ReactiveRedisTemplate<String, RegisteredClient>): ReactiveRegisteredClientRepository {
        return RedisRegisteredClientRepository(redisTemplate)
    }
}