package com.firststory.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginDtoRequest(

    @field:NotBlank(message = "아이디를 입력해주세요.")
    @field:Size(min = 2, max = 20, message = "아이디는 2자 이상, 20자 이하로 입력해주세요.")
    val username: String,

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    val password: String
)
