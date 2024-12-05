package com.sooum.android.data.remote

import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.model.MyProfileDataModel
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {
    @GET("/profiles/my")
    suspend fun getMyProfile(
    ): Response<MyProfileDataModel>

    @GET("/members/feed-cards")
    suspend fun getMyFeedCard(
    ): Response<MyFeedCardDataModel>
}