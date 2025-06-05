package com.grepp.gateway.security.oauth2.user

import org.springframework.security.oauth2.core.user.OAuth2User

interface OAuth2UserInfo {
    // OAuth 제공자별 고유 ID (예: Google의 sub, GitHub의 id)
    val providerId: String

    // OAuth 제공자 이름 (예: "google", "github")
    val provider: String

    val name: String


    // 프로필 이미지 URL (선택 사항)
    val picture: String?

    companion object {
        fun createUserInfo(path: String, user: OAuth2User): OAuth2UserInfo {
            val map = mapOf(
                "/login/oauth2/code/grepp" to GreppOAuth2UserInfo(user.attributes),
                "/login/oauth2/code/github" to  GithubOAuth2UserInfo(user.attributes)
            )
            return map[path]!!
        }
    }
}
