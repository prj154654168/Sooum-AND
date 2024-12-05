package com.sooum.android.domain.model

data class MyProfileDataModel(
    val cardCnt: String,
    val currentDayVisitors: String,
    val followerCnt: String,
    val followingCnt: String,
    val nickname: String,
    val profileImg: ProfileImg?,
    val status: Status,
    val totalVisitorCnt: String
){
    data class ProfileImg(
        val href: String
    )
}