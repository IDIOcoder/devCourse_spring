package com.grepp.gateway.security.oauth2.user

class GithubOAuth2UserInfo(private val attributes: Map<String, Any>) : OAuth2UserInfo {
    override val providerId: String
        get() =  attributes["id"].toString()

    override val provider: String
        get() =  "github"

    override val name: String
        get() =  attributes["login"].toString()

    override val picture: String
        get() =  attributes["avatar_url"].toString()
}
