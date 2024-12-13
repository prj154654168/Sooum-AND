package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class SortedByLatestDataModel(
    @SerializedName("_embedded")
    val embedded: Embedded,
    @SerializedName("_links")
    val links: Links,
    val status: Status
) {
    data class Embedded(
        val latestFeedCardDtoList: List<LatestFeedCard>
    ) {
        data class LatestFeedCard(
            @SerializedName("_links")
            val links: Links,
            val backgroundImgUrl: BackgroundImgUrl,
            val commentCnt: Int,
            val content: String,
            val createdAt: String,
            val distance: Double?,
            val font: String,
            val fontSize: String,
            val id: Long,
            val isCommentWritten: Boolean,
            val isLiked: Boolean,
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