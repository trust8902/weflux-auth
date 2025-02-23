package com.firststory.auth.service

import com.firststory.auth.domain.Member
import com.firststory.auth.dto.SignUpDtoResponse
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService(
    private val memberService: MemberService,
    private val tokenService: TokenService,
    private val authenticationManager: ReactiveAuthenticationManager,
) {
    fun signUp(username: String, password: String, name: String): Mono<SignUpDtoResponse> {
        return memberService.signUp(
            username,
            password,
            name,
            tokenService.createAuthToken(username)
        )
    }

    fun authenticate(username: String, password: String): Mono<Pair<String, String>> {
        val authToken = UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken).flatMap { auth ->
            val user = auth.principal as UserDetails
            memberService.findByUsernameDefault(username)
                .flatMap { member ->
                    val accessToken = tokenService.createAccessToken(member.username)
                    val refreshToken = tokenService.createRefreshToken(member.username)

                    Mono.zip(
                        tokenService.saveAccessToken(member.id!!, accessToken),
                        tokenService.saveRefreshToken(member.id, refreshToken)
                    ).thenReturn(Pair(accessToken, refreshToken))
                }
        }
    }

//    fun refreshToken(refreshToken: String): Mono<Pair<String, String>> {
//        return Mono.fromCallable { tokenService.verifyRefreshToken(refreshToken) }
//            .flatMap { username ->
//                val newAccessToken = tokenService.createAccessToken(username)
//                val newRefreshToken = tokenService.createRefreshToken(username)
//
//                Mono.zip(
//                    tokenService.saveAccessToken(username, newAccessToken),
//                    tokenService.saveRefreshToken(username, newRefreshToken)
//                ).thenReturn(Pair(newAccessToken, newRefreshToken))
//            }
//    }
}