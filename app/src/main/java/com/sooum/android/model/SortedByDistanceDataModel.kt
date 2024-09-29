package com.sooum.android.model

data class SortedByDistanceDataModel(
    val _embedded: Embedded,
    val _links: Links,
    val status: Status
) {
    data class Embedded(
        val distanceCardDtoList: List<DistanceCardDto>
    ) {
        data class DistanceCardDto(
            val _links: Links,
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
            val storyExpirationTime: String
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