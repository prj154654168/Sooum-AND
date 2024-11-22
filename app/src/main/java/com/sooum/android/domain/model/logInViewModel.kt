package com.sooum.android.domain.model

data class logInModel(
    val isRegistered: Boolean,
    val status: Status,
    val token: Token?,
    val links: Links,
) {
    data class Token(
        val accessToken: String,
        val refreshToken: String,
    )

    data class Links(
        val tagFeed: signUp,
    )

    data class signUp(val href: String)
    data class Status(val code: Int, val message: String)
}