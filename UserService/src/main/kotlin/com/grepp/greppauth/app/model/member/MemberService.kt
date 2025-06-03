package com.grepp.greppauth.app.model.member

import com.grepp.greppauth.app.controller.member.payload.SignupRequest
import com.grepp.greppauth.app.model.member.code.Role
import com.grepp.greppauth.app.model.member.entity.Member
import com.grepp.greppauth.app.model.member.entity.MemberInfo
import com.grepp.greppauth.infra.error.CommonException
import com.grepp.greppauth.infra.response.ResponseCode
import lombok.RequiredArgsConstructor
import org.modelmapper.ModelMapper
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class MemberService(
    private val passwordEncoder: PasswordEncoder,
    private val memberRepository: MemberRepository,
    private val mapper: ModelMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun signup(dto: SignupRequest, role: Role) {
        if (memberRepository.existsById(dto.userId)) throw CommonException(ResponseCode.BAD_REQUEST)

        val member = Member(dto.userId,dto.password, dto.email, role, dto.tel)
        val encodedPassword = passwordEncoder.encode(dto.password)

        member.password = encodedPassword
        member.role = role

        val memberInfo = MemberInfo(member.userId)
        member.info = memberInfo
        memberRepository.save(member)
    }

    fun isDuplicatedId(id: String): Boolean {
        return memberRepository.existsById(id)
    }

    fun findById(userId: String): MemberDto {
        val member: Member = memberRepository.findById(userId)
            .orElseThrow { CommonException(ResponseCode.BAD_REQUEST) }
        return mapper.map(member, MemberDto::class.java)
    }
}