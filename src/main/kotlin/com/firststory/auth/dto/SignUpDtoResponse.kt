package com.firststory.auth.dto

import com.firststory.auth.domain.Member
import org.jetbrains.annotations.NotNull
import reactor.core.publisher.Mono

data class SignUpDtoResponse(
    val username: String,
    val name: String,
    val authToken: String
) {
    companion object {
        fun fromEntity(member: Member): Mono<SignUpDtoResponse> {
            return Mono.just(
                SignUpDtoResponse(
                    username = member.username,
                    name = member.name,
                    authToken = member.authToken
                )
            )
        }
    }
}
