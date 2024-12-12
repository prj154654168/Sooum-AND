package com.sooum.android.domain.model

data class DifProfileDataModel(
    val nickname: String,
    val currentDayVisitors: String,
    val totalVisitorCnt: String,
    val profileImg: ProfileImg?,
    val cardCnt: String,
    val followingCnt: String,
    val followerCnt: String,
    val following: Boolean,
    val isFollowing: Boolean,
    val status: Status,
) {
    data class ProfileImg(val href: String)
}

