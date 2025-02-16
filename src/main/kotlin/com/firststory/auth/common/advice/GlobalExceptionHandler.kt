package com.firststory.auth.common.advice

import com.firststory.auth.common.response.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 기본 예외 처리 (IllegalArgumentException, NoSuchElementException 등)
     */
    @ExceptionHandler(value = [
        Exception::class,
        NoSuchElementException::class,
        IllegalArgumentException::class,
    ])
    fun handleGenericException(e: Exception, exchange: ServerWebExchange): Mono<ResponseEntity<ApiResponse<ErrorDetails>>> {
        val httpStatus = when (e) {
            is NoSuchElementException -> HttpStatus.NOT_FOUND
            is IllegalArgumentException -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        return buildErrorResponse(e, httpStatus)
    }

    /**
     * WebFlux 기반 유효성 검증 예외 처리
     */
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationExceptions(e: WebExchangeBindException): Mono<ResponseEntity<ApiResponse<ErrorDetails>>> {
        val fieldErrors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        val errorDetails = ErrorDetails(
            error = "Validation Error",
            message = "Validation failed for request",
            fieldErrors = fieldErrors
        )

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponse(
                statusCode = HttpStatus.BAD_REQUEST.value(),
                data = errorDetails,
            )
        ))
    }

    /**
     * 공통적인 예외 응답을 생성하는 메서드
     */
    private fun buildErrorResponse(
        e: Exception,
        httpStatus: HttpStatus,
        customErrorId: Int? = null
    ): Mono<ResponseEntity<ApiResponse<ErrorDetails>>> {
        logger.error("Exception: ${e.message}", e)

        val errorDetails = ErrorDetails(
            error = (customErrorId ?: httpStatus.reasonPhrase).toString(),
            message = e.message ?: "Unexpected error occurred"
        )

        return Mono.just(ResponseEntity.status(httpStatus).body(
            ApiResponse(
                statusCode = httpStatus.value(),
                data = errorDetails,
            )
        ))
    }

//    @ExceptionHandler(value = [
//        Exception::class,
//        NoSuchElementException::class,
//        IllegalArgumentException::class,
//    ])
//    fun handleGenericException(e: Exception, request: WebRequest): ResponseEntity<ApiResponse<ErrorDetails>> {
//        val httpStatus = when (e) {
//            is NoSuchElementException -> HttpStatus.NOT_FOUND
//            is IllegalArgumentException -> HttpStatus.BAD_REQUEST
//            else -> HttpStatus.INTERNAL_SERVER_ERROR
//        }
//
//        return buildErrorResponse(e, httpStatus)
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException::class)
//    fun handleMethodArgumentNotValidException(
//        e: MethodArgumentNotValidException,
//        request: WebRequest
//    ): ResponseEntity<ApiResponse<ErrorDetails>> {
//        val fieldErrors = e.bindingResult.allErrors
//            .filterIsInstance<FieldError>().associate {
//                it.field to (it.defaultMessage ?: "Invalid value")
//            }
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//            ApiResponse(
//                statusCode = HttpStatus.BAD_REQUEST.value(),
//                data = ErrorDetails(
//                    error = "Validation Error",
//                    message = "Validation faild for request",
//                    fieldErrors = fieldErrors,
//                )
//            )
//        )
//    }
//
//    @ExceptionHandler(WebExchangeBindException::class)
//    fun handleValidationExceptions(e: WebExchangeBindException): ResponseEntity<ApiResponse<ErrorDetails>> {
//        return buildErrorResponse(e, HttpStatus.BAD_REQUEST)
//    }

    data class ErrorDetails(
        val error: String,
        val message: String,
        val fieldErrors: Map<String, String>? = null,
    )
}