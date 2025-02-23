package com.firststory.auth.controller

import com.firststory.auth.dto.ClientRegisterDtoRequest
import com.firststory.auth.dto.ClientRegisterDtoResponse
import com.firststory.auth.service.ClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/oauth2/clients")
class ClientController(
    private val clientService: ClientService
) {
    @PostMapping("/register")
    fun register(@RequestBody dto: ClientRegisterDtoRequest): Mono<ClientRegisterDtoResponse> {
        return clientService.register(dto)
    }
//
//    @PutMapping("/{clientId}")
//    fun update(
//        @PathVariable clientId: String,
//        @RequestBody clientUpdateDtoRequest: ClientUpdateDtoRequest
//    ): Mono<ClientUpdateDtoResponse> {
//        return clientService.update(clientId, clientUpdateDtoRequest)
//    }

//    @GetMapping("/client/{clientId}")
//    fun getClient(@PathVariable clientId: String): Mono<ResponseEntity<RegisteredClient>> {
//        return reactiveRedisTemplate.opsForValue()
//            .get(clientId)  // Redis에서 클라이언트 정보를 조회
//            .cast(RegisteredClient::class.java)
//            .map { ResponseEntity.ok(it) }
//            .defaultIfEmpty(ResponseEntity.notFound().build())
//    }
}