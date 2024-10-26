package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class RelatedTagDataModel(
    @SerializedName("_embedded")
    val embedded: Embedded,
    val status: Status
) {
    data class Embedded(
        val relatedTagList: List<RelatedTag>
    ) {
        data class RelatedTag(
            val content: String,
            val count: Int
        )
    }

    data class Status(
        val httpCode: Int,
        val httpStatus: String,
        val responseMessage: String
    )
}