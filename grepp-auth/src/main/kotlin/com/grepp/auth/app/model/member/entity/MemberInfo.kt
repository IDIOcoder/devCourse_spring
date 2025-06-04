package com.grepp.auth.app.model.member.entity

import com.grepp.spring.infra.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class MemberInfo : BaseEntity() {
    @Id
    private val userId: String? = null
    private val loginDate: LocalDateTime? = null
    private val modifyDate: LocalDateTime? = null
    private val leaveDate: LocalDateTime? = null
    private val rentableDate: LocalDateTime = LocalDateTime.now()
}
