package com.grepp.auth.app.model.member.entity

import com.grepp.auth.app.model.member.code.Role
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Member : BaseEntity() {
    @Id
    private val userId: String? = null
    private val password: String? = null
    private val email: String? = null

    @Enumerated(EnumType.STRING)
    private val role: Role? = null
    private val tel: String? = null

    @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "userId")
    private val info: MemberInfo? = null

    fun updateLoginedAt(time: LocalDateTime?) {
        info.setLoginDate(time)
    }
}
