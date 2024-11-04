package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class DetailCommentCardDataModel(
    @SerializedName("_embedded")
    val embedded: Embedded,
    @SerializedName("_links")
    val links: LinksX,
    val status: Status,
) {
    data class Embedded(
        val commentCardsInfoList: List<CommentCardsInfo>,
    )

    data class Links(
        val detail: Detail,
    )

    data class Detail(
        val href: String,
    )

    data class CommentCardsInfo(
        @SerializedName("_links")
        val links: Links,
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
        val likeCnt: Int,
    )

    data class Next(
        val href: String,
    )

    data class LinksX(
        val next: Next,
    )
}


