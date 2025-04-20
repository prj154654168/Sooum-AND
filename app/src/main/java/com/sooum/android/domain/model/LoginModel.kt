package com.sooum.android.domain.model

data class LoginModel(
    val isRegistered: Boolean,
    val status: Status,
    val token: Token?,
    val _links: Links,
) {
    data class Links(
        val signup: SignUp,
    )

    data class SignUp(val href: String)
    data class Status(val code: Int, val message: String)
}