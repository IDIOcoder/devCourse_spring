package com.grepp.spring.app.model

import com.grepp.spring.app.controller.payload.SmtpRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MailService {
    private val log = LoggerFactory.getLogger(javaClass)

    fun send(request: SmtpRequest){
        TODO("Not yet implemented")
    }
}