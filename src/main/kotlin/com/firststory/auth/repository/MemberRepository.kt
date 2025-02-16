package com.firststory.auth.repository

import com.firststory.auth.domain.Member
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface MemberRepository: ReactiveCrudRepository<Member, Long> {

    fun findByUsername(username: String) : Mono<Member>

}