package com.firststory.auth.controller

import com.firststory.auth.dto.ClientRegisterDtoRequest
import com.firststory.auth.service.ClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/oauth2/clients")
class ClientController(
    private val clientService: ClientService
) {
    @PostMapping("/register")
    fun registerClient(@RequestBody dto: ClientRegisterDtoRequest): Mono<ResponseEntity<String>> {
        return clientService.registerNewClient(
            clientId = dto.clientId,
            rawClientSecret = dto.clientSecret,
            clientName = dto.clientName,
            redirectUri = dto.redirectUri,
            scopes = dto.scopes,
            grantTypes = dto.grantTypes
        ).thenReturn(ResponseEntity.ok("Client registered successfully"))
            .onErrorResume { e ->
                Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering client: ${e.message}"))
            }
    }

//    @GetMapping("/client/{clientId}")
//    fun getClient(@PathVariable clientId: String): Mono<ResponseEntity<RegisteredClient>> {
//        return reactiveRedisTemplate.opsForValue()
//            .get(clientId)  // Redis에서 클라이언트 정보를 조회
//            .cast(RegisteredClient::class.java)
//            .map { ResponseEntity.ok(it) }
//            .defaultIfEmpty(ResponseEntity.notFound().build())
//    }
}