package com.grepp.gateway.infra.error

import com.grepp.greppauth.infra.response.ApiResponse
import com.grepp.greppauth.infra.response.ResponseCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Consumer

@RestControllerAdvice
class RestApiExceptionAdvice {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validatorHandler(ex: MethodArgumentNotValidException)
        : ResponseEntity<ApiResponse<MutableMap<String, String?>>> {

        log.info(ex.message, ex)

        val errors: MutableMap<String, String?> = LinkedHashMap()
        ex.fieldErrors.forEach(Consumer { e: FieldError ->
            errors[e.field] = e.defaultMessage
        })
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(ResponseCode.BAD_REQUEST, errors))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun methodNotSupportedHandler(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<String?>> {
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.error(ResponseCode.BAD_REQUEST, ex.message))
    }

    @ExceptionHandler(CommonException::class)
    fun restApiExceptionHandler(ex: CommonException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity
            .status(ex.code().status())
            .body(ApiResponse.error(ex.code()))
    }

    @ExceptionHandler(RuntimeException::class)
    fun runtimeExceptionHandler(ex: RuntimeException?): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR))
    }
}