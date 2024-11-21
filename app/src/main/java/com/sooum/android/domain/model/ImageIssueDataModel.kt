package com.sooum.android.domain.model

data class ImageIssueDataModel(
    val imgName: String,
    val status: Status,
    val url: Url
) {
    data class Status(
        val httpCode: Int,
        val httpStatus: String,
        val responseMessage: String
    )

    data class Url(
        val href: String
    )
}