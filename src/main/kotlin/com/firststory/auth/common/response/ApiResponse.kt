package com.firststory.auth.common.response

data class ApiResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T?,
)
