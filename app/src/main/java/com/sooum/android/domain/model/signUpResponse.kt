package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class signUpResponse(
    val _links: Links,
    val status: Status,
    val token: Token,
) {
    data class Links(
        val home: home,
    )

    data class home(
        @SerializedName("tagfeed")
        val href: String,
    )
}

data class Token(
    val accessToken: String,
    val refreshToken: String,
)