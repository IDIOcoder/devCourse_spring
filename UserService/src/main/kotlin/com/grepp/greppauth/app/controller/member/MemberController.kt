package com.grepp.greppauth.app.controller.member

import com.grepp.greppauth.app.controller.member.payload.SignupRequest
import com.grepp.greppauth.app.model.member.MemberDto
import com.grepp.greppauth.app.model.member.MemberService
import com.grepp.greppauth.app.model.member.code.Role
import com.grepp.greppauth.infra.response.ApiResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/member")
class MemberController(
    private val memberService: MemberService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("exists/{id}")
    fun existsId(@PathVariable id: String): ResponseEntity<ApiResponse<Boolean>> {
        return ResponseEntity.ok(
            ApiResponse.success(
                memberService.isDuplicatedId(id)
            )
        )
    }

    @PostMapping("add")
    fun signup(
        @Valid
        form: SignupRequest,
        bindingResult: BindingResult,
    ): ResponseEntity<ApiResponse<Unit>> {
        memberService.signup(form, Role.ROLE_USER)
        return ResponseEntity.ok(ApiResponse.noContent())
    }

    @GetMapping("{id}")
    fun get(@PathVariable id:String): ResponseEntity<ApiResponse<MemberDto>> {
        val memberDto: MemberDto = memberService.findById(id)
        return ResponseEntity.ok(ApiResponse.success(memberDto))
    }
}