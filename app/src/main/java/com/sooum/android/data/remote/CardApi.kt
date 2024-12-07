package com.sooum.android.data.remote

import com.sooum.android.domain.model.BlockBody
import com.sooum.android.domain.model.DefaultImageDataModel
import com.sooum.android.domain.model.DetailCardLikeCommentCountDataModel
import com.sooum.android.domain.model.DetailCommentCardDataModel
import com.sooum.android.domain.model.EncryptedDeviceId
import com.sooum.android.domain.model.FeedCardDataModel
import com.sooum.android.domain.model.ImageIssueDataModel
import com.sooum.android.domain.model.KeyModel
import com.sooum.android.domain.model.PostCommentCardRequestDataModel
import com.sooum.android.domain.model.PostFeedRequestDataModel
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.model.TagFeedDataModel
import com.sooum.android.domain.model.logInModel
import com.sooum.android.domain.model.profileBody
import com.sooum.android.domain.model.signUpModel
import com.sooum.android.domain.model.signUpResponse
import com.sooum.android.enums.DistanceEnum
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CardApi {
    @GET("/cards/home/latest")
    suspend fun getLatestCardList(

        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
    ): Response<SortedByLatestDataModel>

    @GET("/cards/home/popular")
    suspend fun getPopularityCardList(

        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
    ): Response<SortedByPopularityDataModel>

    @GET("/cards/home/distance")
    suspend fun getDistanceCardList(

        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("distanceFilter") distance: DistanceEnum,
    ): Response<SortedByDistanceDataModel>

    @GET("/cards/{cardId}/detail")
    suspend fun getFeedCard(
        @Path("cardId") cardId: Long,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
    ): Response<FeedCardDataModel>

    @GET("/cards/current/{currentCardId}/summary")
    suspend fun getCardLikeCommentCount(
        @Path("currentCardId") cardId: Long,
    ): Response<DetailCardLikeCommentCountDataModel>

    @GET("/comments/current/{currentCardId}")
    suspend fun getDeatilCommentCard(
        @Path("currentCardId") cardId: Long,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,

    ): Response<DetailCommentCardDataModel>

    @POST("/cards/{cardId}/like")
    suspend fun likeOn(

        @Path("cardId") cardId: Long,
    ): Response<Status>

    @DELETE("/cards/{cardId}/like")
    suspend fun likeOff(

        @Path("cardId") cardId: Long,
    ): Response<Status>

    @GET("/imgs/default")
    suspend fun getDefaultImage(

        @Query("previousImgsName") previousImgsName: String? = null,
    ): Response<DefaultImageDataModel>

    @POST("/blocks")
    suspend fun userBlocks(
        @Body toMemberId: BlockBody,
    ): Response<Status>

    @DELETE("/cards/{cardId}")
    suspend fun deleteCard(

        @Path("cardId") cardId: Long,
    ): Response<Status>

    @GET("/imgs/cards/upload?extension=jpeg")
    suspend fun getImageUrl(

    ): Response<ImageIssueDataModel>

    @POST("/cards")
    suspend fun postFeedCard(
        @Body request: PostFeedRequestDataModel
        ): Response<Status>
    @GET("/users/key")
    suspend fun getRsaKey(
    ): Response<KeyModel>

    @POST("/users/login")
    suspend fun logIn(
        @Body encryptedDeviceId : EncryptedDeviceId
    ): Response<logInModel>

    @POST("/users/sign-up")
    suspend fun signUp(
        @Body encryptedDeviceId : signUpModel
    ): Response<signUpResponse>

    @PATCH("/profiles")
    suspend fun profiles(
        @Body profileBody : profileBody
    ): Response<Status>

    @POST("/cards/{cardId}")
    suspend fun postCommentCard(
        @Path("cardId") cardId: Long,
        @Body postCommentCardRequest: PostCommentCardRequestDataModel
    ) : Response<Status>

    @GET("/cards/tags/{tagId}")
    suspend fun getTagFeed(
        @Path("tagId") tagId: String,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("lastPk") lastPk: Long? = null
    ): Response<TagFeedDataModel>
}