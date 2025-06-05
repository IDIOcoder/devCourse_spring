package com.grepp.spring.app.controller.api.member

import com.grepp.spring.infra.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("member")
class MemberController {
    @PostMapping("signin")
    fun signin():ResponseEntity<ApiResponse<Unit>>{
        return ResponseEntity.ok(ApiResponse.noContent())
    }
}