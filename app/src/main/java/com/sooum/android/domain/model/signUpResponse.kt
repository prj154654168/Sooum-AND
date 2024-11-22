package com.sooum.android.domain.model

data class signUpResponse(
    val _links: Links,
    val status: Status,
    val token: Token
){

}

data class Token(
    val accessToken: String,
    val refreshToken: String
)