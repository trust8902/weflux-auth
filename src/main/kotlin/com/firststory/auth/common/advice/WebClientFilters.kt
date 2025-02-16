package com.firststory.auth.common.advice

import com.fasterxml.jackson.databind.ObjectMapper
import com.firststory.auth.common.response.ApiResponse
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

object WebClientFilters {

    private val objectMapper = ObjectMapper()
    private val logger: Logger = LoggerFactory.getLogger(WebClientFilters::class.java)

    fun responseWrapper(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            clientResponse.bodyToMono(Any::class.java)
                .map { body ->
                    ApiResponse(
                        statusCode = clientResponse.statusCode().value(),
                        data = body
                    )
                }
                .flatMap { wrapperResponse ->
                    // JSON 변환
                    val jsonString = objectMapper.writeValueAsString(wrapperResponse)
                    val dataBuffer: DataBuffer = DefaultDataBufferFactory().wrap(jsonString.toByteArray(StandardCharsets.UTF_8))

                    // Mono<DataBuffer> -> Flux<DataBuffer> 변환
                    val bodyFlux: Flux<DataBuffer> = Flux.just(dataBuffer)

                    // 새로운 ClientResponse 생성
                    Mono.just(
                        ClientResponse.create(clientResponse.statusCode())
                            .headers { headers -> headers.addAll(clientResponse.headers().asHttpHeaders()) }
                            .body(bodyFlux) // Flux<DataBuffer> 사용
                            .build()
                    )
                }
        }
    }

    fun errorHandling(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            if (clientResponse.statusCode().isError) {
                logger.error("Error Response: ${clientResponse.statusCode()}")
            }
            Mono.just(clientResponse)
        }
    }

    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .filter(responseWrapper())  // 모든 응답을 감싸는 필터
            .filter(errorHandling())   // 에러 로깅 필터
            .build()
    }

}