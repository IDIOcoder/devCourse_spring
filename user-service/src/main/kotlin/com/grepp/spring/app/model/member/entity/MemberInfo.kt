package com.grepp.spring.app.model.member.entity

import com.grepp.spring.infra.entity.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class MemberInfo(
    @Id
    private val userId: String,
    private var loginDate: LocalDateTime = LocalDateTime.now(),
    private var modifyDate: LocalDateTime? = null,
    private var leaveDate: LocalDateTime? = null,
    private var rentableDate: LocalDateTime = LocalDateTime.now()
) : BaseEntity() {

}
