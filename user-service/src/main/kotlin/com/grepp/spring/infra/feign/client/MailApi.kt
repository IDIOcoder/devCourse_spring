package com.grepp.spring.infra.feign.client

import com.grepp.spring.app.model.member.dto.SmtpDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "mail-service",
    url = "http://localhost:8080/mail/"
)
interface MailApi {
    @PostMapping("send")
    fun sendMail(
        @RequestHeader(name="x-member-id")
        userId:String = "user-service",
        @RequestHeader(name="x-member-role")
        role:String = "ROLE_SERVER",
        payload:SmtpDto
    ){

    }
}