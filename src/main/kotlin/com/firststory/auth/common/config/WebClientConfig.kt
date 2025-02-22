package com.firststory.auth.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder
            .filter(logRequest())
            .filter(logResponse())
            .build()
    }

    private fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { request ->
            println("Request: \${request.method()} \${request.url()}")
            request.headers().forEach { name, values ->
                values.forEach { value -> println("\$name: \$value") }
            }
            Mono.just(request)
        }
    }

    private fun logResponse(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { response ->
            println("Response Status: \${response.statusCode()}")
            response.headers().asHttpHeaders().forEach { name, values ->
                values.forEach { value -> println("\$name: \$value") }
            }
            Mono.just(response)
        }
    }

}