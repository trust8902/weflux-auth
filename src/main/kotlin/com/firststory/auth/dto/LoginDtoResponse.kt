package com.firststory.auth.dto

data class LoginDtoResponse(
    val accessToken: String,
    val refreshToken: String
)
