package com.sooum.android.domain.model

data class KeyModel(
    val status: Status,
    val publicKey: String,
    val _links: Links,
) {
    data class Links(
        val login: Login,
    )

    data class Login(
        val href: String,
    )
}
