package com.grepp.greppauth.app.model.member.entity

import com.grepp.greppauth.app.model.member.code.Role
import com.grepp.greppauth.infra.entity.BaseEntity
import jakarta.persistence.*
import lombok.Getter
import lombok.Setter
import lombok.ToString
import java.time.LocalDateTime

@Getter
@Setter
@ToString
@Entity
class Member(
    @Id
    val userId: String,
    var password: String,
    var email: String,

    @Enumerated(EnumType.STRING)
    var role: Role,
    var tel: String,

    @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "userId")
    var info: MemberInfo? = null,

) : BaseEntity() {
    fun updateLoginedAt(time: LocalDateTime) {
        this.info?.loginDate = time
    }
}
