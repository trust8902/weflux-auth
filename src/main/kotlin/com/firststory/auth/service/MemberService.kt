package com.firststory.auth.service

import com.firststory.auth.domain.Member
import com.firststory.auth.repository.MemberRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val tokenService: TokenService
) {

    fun getMember(username: String): Mono<Member> {
        return memberRepository.findByUsername(username)
    }

    fun registerMember(username: String, password: String, name: String): Mono<Member> {
        val authToken = tokenService.generateAuthToken()

        val member = Member(
            username = username,
            password = password,
            name = name,
            authToken = authToken
        )

        return memberRepository.save(member)
    }

    fun authenticate(username: String, password: String): Mono<String> {
        return memberRepository.findByUsername(username)
            .filter { it.password == password }
            .flatMap {
                val accessToken = tokenService.generateAccessToken(username)
                tokenService.saveAccessToken(username, accessToken)
                    .thenReturn(accessToken)
            }
    }

}