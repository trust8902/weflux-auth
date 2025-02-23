package com.firststory.auth.controller

import com.firststory.auth.common.response.ApiResponse
import com.firststory.auth.dto.*
import com.firststory.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@Validated
@RestController
@RequestMapping("/oauth2")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signUp")
    fun signUp(@RequestBody @Valid dto: SignUpDtoRequest): Mono<SignUpDtoResponse> {
        return authService.signUp(dto.username, dto.password, dto.name)
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid dto: LoginDtoRequest): Mono<LoginDtoResponse> {
        return authService.authenticate(dto.username, dto.password)
            .map { (accessToken, refreshToken) ->
                LoginDtoResponse(accessToken, refreshToken)
            }
    }

//    @PostMapping("/refresh")
//    fun refreshToken(@RequestBody @Valid dto: RefreshTokenDtoRequest): Mono<RefreshTokenDtoResponse> {
//        return authService.refreshToken(dto.refreshToken)
//            .map { (accessToken, refreshToken) ->
//                RefreshTokenDtoResponse(accessToken, refreshToken)
//            }
//    }
}