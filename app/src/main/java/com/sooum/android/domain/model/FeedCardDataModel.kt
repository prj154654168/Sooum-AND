package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class FeedCardDataModel(
    val backgroundImgUrl: BackgroundImgUrl,
    val content: String,
    val createdAt: String,
    val distance: Double,
    val font: String,
    val id: String,
    val isOwnCard: Boolean,
    val isStory: Boolean,
    val member: Member,
    val status: Status,
    val storyExpirationTime: String?,
    val tags: List<Tag>,
)

data class BackgroundImgUrl(
    val href: String,
)

data class Links(
    @SerializedName("tagfeed")
    val tagFeed: TagFeed,
)

data class Member(
    val id: String,
    val nickname: String,
    val profileImgUrl: String?,
)

data class Status(
    val httpCode: Int,
    val httpStatus: String,
    val responseMessage: String,
)

data class Tag(
    @SerializedName("_links")
    val links: Links,
    val content: String,
    val id: String,
)

data class TagFeed(
    val href: String,
)