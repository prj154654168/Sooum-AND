package com.sooum.android.data.remote

import com.sooum.android.domain.model.CodeDataModel
import com.sooum.android.domain.model.MyCommentCardDataModel
import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.model.MyProfileDataModel
import com.sooum.android.domain.model.NoticeDataModel
import com.sooum.android.domain.model.UserCodeBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ProfileApi {
    @GET("/profiles/my")
    suspend fun getMyProfile(
    ): Response<MyProfileDataModel>

    @GET("/members/feed-cards")
    suspend fun getMyFeedCard(
    ): Response<MyFeedCardDataModel>

    @GET("/members/comment-cards")
    suspend fun getMyCommentCard(
    ): Response<MyCommentCardDataModel>

    @GET("/notices")
    suspend fun getNotice(
    ): Response<NoticeDataModel>

    @DELETE("/members")
    suspend fun deleteUser()

    @GET("/settings/transfer")
    suspend fun getCode(): Response<CodeDataModel>

    @PATCH("/settings/transfer")
    suspend fun patchCode(): Response<CodeDataModel>

    @POST("/settings/transfer")
    suspend fun postCode(@Body uerCodeBody: UserCodeBody)
}