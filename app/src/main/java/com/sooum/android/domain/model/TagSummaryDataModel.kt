package com.sooum.android.domain.model

data class TagSummaryDataModel(
    val cardCnt: Int,
    val content: String,
    val isFavorite: Boolean,
    val status: Status
) {
    data class Status(
        val httpCode: Int,
        val httpStatus: String,
        val responseMessage: String
    )
}