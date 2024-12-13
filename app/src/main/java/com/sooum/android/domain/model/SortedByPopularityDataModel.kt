package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class SortedByPopularityDataModel(
    @SerializedName("_embedded")
    val embedded: Embedded,
    val status: Status
) {
    data class Embedded(
        val popularCardRetrieveList: List<PopularFeedCard>
    ) {
        data class PopularFeedCard(
            val id: Long,
            val content: String,
            val storyExpiredTime: String?,
            val backgroundImgUrl: BackgroundImgUrl,
            val font: String,
            val fontSize: String,
            val distance: Double?,
            val createdAt: String,
            val likeCnt: Int,
            val commentCnt: Int,
            @SerializedName("_links")
            val _links: Links,
            val isLiked: Boolean,
            val isCommentWritten: Boolean
        ) {
            data class BackgroundImgUrl(
                val href: String
            )
            data class Links(
                val detail: Detail
            ) {
                data class Detail(
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