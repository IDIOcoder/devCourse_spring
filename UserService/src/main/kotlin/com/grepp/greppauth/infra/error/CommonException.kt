package com.grepp.greppauth.infra.error

import com.grepp.greppauth.infra.response.ResponseCode
import org.slf4j.LoggerFactory

class CommonException : RuntimeException {
    private val code: ResponseCode
    private val log = LoggerFactory.getLogger(javaClass)

    constructor(code: ResponseCode) {
        this.code = code
    }

    constructor(code: ResponseCode, e: Exception) {
        this.code = code
        log.error(e.message, e)
    }

    fun code(): ResponseCode {
        return code
    }
}
