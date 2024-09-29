package com.sooum.android.model

data class SortedByPopularityDataModel(
    val _embedded: Embedded,
    val status: Status
) {
    data class Embedded(
        val popularCardRetrieveList: List<PopularCard>
    ) {
        data class PopularCard(
            val id: Long,
            val content: String,
            val storyExpiredTime: String?,
            val backgroundImgUrl: BackgroundImgUrl,
            val font: String,
            val fontSize: String,
            val distance: Double,
            val createdAt: String,
            val likeCnt: Int,
            val commentCnt: Int,
            val popularityType: String? = null, // Optional for some cards
            val _links: Links,
            val isStory: Boolean,
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