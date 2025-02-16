package com.firststory.auth.dto

data class RefreshTokenDtoResponse(
    val accessToken: String,
    val refreshToken: String
)
