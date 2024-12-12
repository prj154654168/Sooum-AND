package com.sooum.android.domain.model

data class FollowingDataModel(
    val _embedded: Embedded,
    val _links: Links,
    val status: Status,
) {
    data class Embedded(
        val followingInfoList: List<FollowingInfo>,
    )

    data class FollowingInfo(
        val id: String,
        val nickname: String,
        val backgroundImgUrl: BackgroundImgUrl?,
        val following: Boolean,
        val _links: ProfileLinks,
        val isFollowing: Boolean,
    )

    data class BackgroundImgUrl(
        val href: String,
    )

    data class ProfileLinks(
        val profile: Profile,
    )

    data class Profile(
        val href: String,
    )

    data class Links(
        val next: Next,
    )

    data class Next(
        val href: String,
    )

}

