package com.sooum.android.model

import com.google.gson.annotations.SerializedName

data class SortedByDistanceDataModel(
    @SerializedName("_embedded")
    val embedded: Embedded,
    @SerializedName("_links")
    val links: Links,
    val status: Status
) {
    data class Embedded(
        val distanceCardDtoList: List<DistanceFeedCard>
    ) {
        data class DistanceFeedCard(
            @SerializedName("_links")
            val links: Links,
            val backgroundImgUrl: BackgroundImgUrl,
            val commentCnt: Int,
            val content: String,
            val createdAt: String,
            val distance: Double,
            val font: String,
            val fontSize: String,
            val id: Long,
            val isCommentWritten: Boolean,
            val isLiked: Boolean,
            val isStory: Boolean,
            val likeCnt: Int,
            val storyExpirationTime: String?
        ) {
            data class Links(
                val detail: Detail
            ) {
                data class Detail(
                    val href: String
                )
            }
            data class BackgroundImgUrl(
                val href: String
            )
        }
    }

    data class Links(
        val next: Next
    ) {
        data class Next(
            val href: String
        )
    }

    data class Status(
        val httpCode: Int,
        val httpStatus: String,
        val responseMessage: String
    )
}