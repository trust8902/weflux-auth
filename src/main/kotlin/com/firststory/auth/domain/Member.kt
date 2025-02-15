package com.firststory.auth.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("member")
data class Member(
    @Id
    val id: Long? = null,
    val userName: String,
    val password: String,
    val name: String,
    val authToken: String
)
