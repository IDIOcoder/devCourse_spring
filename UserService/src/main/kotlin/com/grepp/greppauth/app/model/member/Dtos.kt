package com.grepp.greppauth.app.model.member

import com.grepp.greppauth.app.model.member.code.Role
import java.time.LocalDateTime

data class MemberDto(
    var userId: String,
    var password: String,
    var email: String,
    var role: Role,
    var tel: String,
)

data class MemberInfoDto(
    var userId: String? = null,
    var loginDate: LocalDateTime? = null,
    var modifyDate: LocalDateTime? = null,
    var leaveDate: LocalDateTime? = null,
    var rentableDate: LocalDateTime? = null
)