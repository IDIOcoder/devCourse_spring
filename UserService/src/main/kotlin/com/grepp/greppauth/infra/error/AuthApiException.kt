package com.grepp.greppauth.infra.error

import com.grepp.spring.infra.response.ResponseCode

class AuthApiException : CommonException {
    constructor(code: ResponseCode?) : super(code)
    constructor(code: ResponseCode?, e: Exception?) : super(code, e!!)
}
