package com.grepp.greppauth.app.controller.member.payload

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignupRequest(
    @NotBlank
    var userId: String,
    @NotBlank
    var password: String,
    @NotBlank
    @Email
    var email: String,
    @NotBlank
    @Size(min = 8, max = 14)
    var tel: String,
) {
}