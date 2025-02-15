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

    fun registerMember(userName: String, password: String, name: String): Mono<Member> {
        val authToken = tokenService.generateAuthToken()

        val member = Member(
            userName = userName,
            password = password,
            name = name,
            authToken = authToken
        )

        return memberRepository.save(member)
    }

    fun authenticate(userName: String, password: String): Mono<String> {
        return memberRepository.findByUserName(userName)
            .filter { it.password == password }
            .flatMap {
                val accessToken = tokenService.generateAccessToken(userName)
                tokenService.saveAccessToken(userName, accessToken)
                    .thenReturn(accessToken)
            }
    }

}