package com.firststory.auth.controller

import com.firststory.auth.domain.Member
import com.firststory.auth.dto.*
import com.firststory.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(@RequestBody @Valid dto: RegisterDtoRequest): Mono<Member> {
        return authService.register(dto.username, dto.password, dto.name)
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid dto: LoginDtoRequest): Mono<LoginDtoResponse> {
        return authService.authenticate(dto.username, dto.password)
            .map { (accessToken, refreshToken) ->
                LoginDtoResponse(accessToken, refreshToken)
            }
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody @Valid dto: RefreshTokenDtoRequest): Mono<RefreshTokenDtoResponse> {
        return authService.refreshToken(dto.refreshToken)
            .map { (accessToken, refreshToken) ->
                RefreshTokenDtoResponse(accessToken, refreshToken)
            }
    }
}