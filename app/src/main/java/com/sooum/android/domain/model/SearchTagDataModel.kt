package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class SearchTagDataModel(
    @SerializedName("_embedded")
    val embedded: Embedded,
    val status: Status
) {
    data class Embedded(
        val relatedTagList: List<RelatedTag>
    ) {
        data class RelatedTag(
            val content: String,
            val count: Int,
            val tagId: String
        )
    }

    data class Status(
        val httpCode: Int,
        val httpStatus: String,
        val responseMessage: String
    )
}