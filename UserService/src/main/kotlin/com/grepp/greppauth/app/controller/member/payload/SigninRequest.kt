package com.grepp.greppauth.app.controller.member.payload

import jakarta.validation.constraints.NotBlank

data class SigninRequest(
    @NotBlank
    private var userId: String,
    @NotBlank
    private val password: String
) {
}