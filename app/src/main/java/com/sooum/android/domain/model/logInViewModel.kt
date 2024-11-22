package com.sooum.android.domain.model

data class logInModel(
    val isRegistered: Boolean,
    val status: Status,
    val token: Token?,
    val _links: Links,
) {
    data class Links(
        val signup: signUp,
    )

    data class signUp(val href: String)
    data class Status(val code: Int, val message: String)
}