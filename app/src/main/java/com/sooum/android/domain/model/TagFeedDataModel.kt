package com.sooum.android.domain.model

data class TagFeedDataModel(
    val _embedded: Embedded,
    val _links: Links,
    val status: Status
) {
    data class Embedded(
        val tagFeedCardDtoList: List<TagFeedCardDto>
    ) {
        data class TagFeedCardDto(
            val _links: Links,
            val backgroundImgUrl: BackgroundImgUrl,
            val commentCnt: Int,
            val content: String,
            val createdAt: String,
            val distance: Double?,
            val font: String,
            val fontSize: String,
            val id: String,
            val isCommentWritten: Boolean,
            val isLiked: Boolean,
            val likeCnt: Int
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