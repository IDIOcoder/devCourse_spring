package com.grepp.gateway.infra.error

import com.grepp.greppauth.infra.response.ResponseCode

class AuthApiException : CommonException {
    constructor(code: ResponseCode) : super(code)
    constructor(code: ResponseCode, e: Exception?) : super(code, e!!)
}
