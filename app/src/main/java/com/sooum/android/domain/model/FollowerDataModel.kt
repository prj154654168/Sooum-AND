package com.sooum.android.domain.model

data class FollowerDataModel(
    val _embedded: Embedded,
    val _links: Links,
    val status: Status
){
    data class Embedded(
        val followerInfoList: List<FollowerInfo>
    )

    data class FollowerInfo(
        val id: String,
        val nickname: String,
        val backgroundImgUrl: String?,
        val following: Boolean,
        val _links: ProfileLinks,
        val isFollowing: Boolean
    )
    {
        data class ProfileLinks(
            val profile: Profile
        )

        data class Profile(
            val href: String
        )

        data class Links(
            val next: Next
        )

        data class Next(
            val href: String
        )
    }
}


