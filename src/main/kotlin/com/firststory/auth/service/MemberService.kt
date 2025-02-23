package com.firststory.auth.service

import com.firststory.auth.domain.Member
import com.firststory.auth.dto.SignUpDtoResponse
import com.firststory.auth.repository.MemberRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return memberRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found")))
            .map { User(it.username, it.password, emptyList()) }
    }

    fun findByUsernameDefault(username: String): Mono<Member> {
        return memberRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(UsernameNotFoundException("User not found")))
    }

    fun signUp(username: String, password: String, name: String, authToken: String): Mono<SignUpDtoResponse> {
        val memberExists = memberRepository.existsByUsername(username)

//        if (memberExists) {
//            Mono.error(UsernameNotFoundException("User not found"))
//        }

        val member = Member(
            username = username,
            password = passwordEncoder.encode(password),
            name = name,
            authToken = authToken
        )

        return memberRepository.save(member)
            .then(SignUpDtoResponse.fromEntity(member))
    }

    fun updateAuthToken(memberId: Long, authToken: String): Mono<Member> {
        return memberRepository.findById(memberId)
            .flatMap { memberRepository.save(it.copy(authToken = authToken)) }
    }

//    fun authenticate(username: String, password: String): Mono<String> {
//        return memberRepository.findByUsername(username)
//            .filter { it.password == password }
//            .flatMap {
//                val accessToken = tokenService.createAccessToken(username)
//                tokenService.saveAccessToken(username, accessToken)
//                    .thenReturn(accessToken)
//            }
//    }

}