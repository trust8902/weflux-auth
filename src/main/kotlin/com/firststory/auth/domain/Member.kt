package com.firststory.auth.domain

import org.jetbrains.annotations.NotNull
import org.springframework.context.annotation.Description
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("member")
@Description("사용자")
data class Member(
    @Id
    val id: Long? = null,

    @NotNull
    val username: String,

    @NotNull
    val password: String,

    @NotNull
    val name: String,

    @NotNull
    val authToken: String
)
