package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class RecommendTagDataModel(
    @SerializedName("_embedded")
    val embedded: Embedded,
    val status: Status
) {
    data class Embedded(
        val recommendTagList: List<RecommendTag>
    ) {
        data class RecommendTag(
            @SerializedName("_links")
            val links: Links,
            val tagContent: String,
            val tagId: String,
            val tagUsageCnt: String
        ) {
            data class Links(
                @SerializedName("tag-feed")
                val tagFeed: TagFeed
            ) {
                data class TagFeed(
                    val href: String
                )
            }
        }
    }

    data class Status(
        val httpCode: Int,
        val httpStatus: String,
        val responseMessage: String
    )
}