package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class FeedCardDataModel(
    val backgroundImgUrl: BackgroundImgUrl,
    val commentCnt: Int,
    val content: String,
    val createdAt: String,
    val distance: Double,
    val font: String,
    val fontSize: String,
    val id: String,
    val isCommentWritten: Boolean,
    val isLiked: Boolean,
    val isOwnCard: Boolean,
    val likeCnt: Int,
    val member: Member,
    val status: Status,
    val storyExpirationTime: String?,
    val tags: List<Tag>,
    val isParentDeleted: Boolean,
    val previousCardId: Long?,
    val previousCardImgLink: PreviousCardImgLink?,

    ) {


}
data class PreviousCardImgLink(
    val href: String,
)
data class BackgroundImgUrl(
    val href: String,
)

data class Links(
    @SerializedName("tag-feed")
    val tagFeed: TagFeed,
)

data class Member(
    val id: String,
    val nickname: String,
    val profileImgUrl: Any,
)

data class Status(
    val httpCode: Int,
    val httpStatus: String,
    val responseMessage: String,
)

data class Tag(
    val _links: Links,
    val content: String,
    val id: String,
)

data class TagFeed(
    val href: String,
)

