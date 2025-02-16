package com.firststory.auth.dto

import jakarta.validation.constraints.NotBlank

data class RefreshTokenDtoRequest(
    @field:NotBlank(message = "refreshToken을 입력해주세요.")
    val refreshToken: String
)
