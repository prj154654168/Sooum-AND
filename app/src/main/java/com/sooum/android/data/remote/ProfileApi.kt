package com.sooum.android.data.remote

import com.sooum.android.Constants
import com.sooum.android.domain.model.CodeDataModel
import com.sooum.android.domain.model.DeleteUserBody
import com.sooum.android.domain.model.DifProfileDataModel
import com.sooum.android.domain.model.FollowerBody
import com.sooum.android.domain.model.FollowerDataModel
import com.sooum.android.domain.model.FollowingDataModel
import com.sooum.android.domain.model.MyCommentCardDataModel
import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.model.MyProfileDataModel
import com.sooum.android.domain.model.NoticeDataModel
import com.sooum.android.domain.model.UserCodeBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileApi {
    @GET("/profiles/my")
    suspend fun getMyProfile(
    ): Response<MyProfileDataModel>

    @GET("/members/feed-cards")
    suspend fun getMyFeedCard(
    ): Response<MyFeedCardDataModel>

    @GET("/profiles/{memberId}")
    suspend fun getDifProfile(
        @Path("memberId") memberId: Long,
    ): Response<DifProfileDataModel>

    @GET("/members/{targetMemberId}/feed-cards")
    suspend fun getDifFeedCard(
        @Path("targetMemberId") targetMemberId: Long,
    ): Response<MyFeedCardDataModel>

    @GET("/members/comment-cards")
    suspend fun getMyCommentCard(
    ): Response<MyCommentCardDataModel>

    @GET("/notices")
    suspend fun getNotice(
    ): Response<NoticeDataModel>

    //@DELETE("/members",hasBody = true)
    @HTTP(method = "DELETE", path = "${Constants.BASE_URL}/members", hasBody = true)
    suspend fun deleteUser(@Body deleteUserBody: DeleteUserBody)

    @GET("/settings/transfer")
    suspend fun getCode(): Response<CodeDataModel>

    @PATCH("/settings/transfer")
    suspend fun patchCode(): Response<CodeDataModel>

    @POST("/settings/transfer")
    suspend fun postCode(@Body uerCodeBody: UserCodeBody)

    @GET("/profiles/follower")
    suspend fun getFollower(): Response<FollowerDataModel>

    @GET("/profiles/{profileOnwerPk}/follower")
    suspend fun getDifFollower(@Path("profileOnwerPk") profileOwnerPk: Long): Response<FollowerDataModel>

    @GET("/profiles/following")
    suspend fun getFollowing(): Response<FollowingDataModel>

    @GET("/profiles/{profileOnwerPk}/following")
    suspend fun getDifFollowing(@Path("profileOnwerPk") profileOwnerPk: Long): Response<FollowingDataModel>

    @POST("/followers")
    suspend fun postFollower(@Body followerBody: FollowerBody) : Response<Any>

    @DELETE("/followers/{toMemberId}")
    suspend fun deleteFollower(@Path("toMemberId") toMemberId: Long) : Response<Any>

}