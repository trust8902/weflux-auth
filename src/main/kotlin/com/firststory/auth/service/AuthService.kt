package com.firststory.auth.service

import com.firststory.auth.domain.Member
import com.firststory.auth.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder
) {
    fun register(username: String, password: String, name: String): Mono<Member> {
        val authToken = tokenService.generateAuthToken()
        val encodedPassword = passwordEncoder.encode(password)
        return memberRepository.save(Member(
            username = username,
            password = encodedPassword,
            name = name,
            authToken = authToken
        ))
    }

    fun authenticate(username: String, password: String): Mono<Pair<String, String>> {
        return memberRepository.findByUsername(username)
            .filter { passwordEncoder.matches(password, it.password) }
            .flatMap {
                val accessToken = tokenService.generateAccessToken(username)
                val refreshToken = tokenService.generateRefreshToken(username)

                Mono.zip(
                    tokenService.saveAccessToken(username, accessToken),
                    tokenService.saveRefreshToken(username, refreshToken)
                ).thenReturn(Pair(accessToken, refreshToken))
            }
    }

    fun refreshToken(refreshToken: String): Mono<Pair<String, String>> {
        return Mono.fromCallable { tokenService.verifyRefreshToken(refreshToken) }
            .flatMap { username ->
                val newAccessToken = tokenService.generateAccessToken(username)
                val newRefreshToken = tokenService.generateRefreshToken(username)

                Mono.zip(
                    tokenService.saveAccessToken(username, newAccessToken),
                    tokenService.saveRefreshToken(username, newRefreshToken)
                ).thenReturn(Pair(newAccessToken, newRefreshToken))
            }
    }
}