package com.sooum.android.data.remote

import com.sooum.android.enums.DistanceEnum
import com.sooum.android.enums.ReportTypeEnum
import com.sooum.android.domain.model.DefaultImageDataModel
import com.sooum.android.domain.model.DetailCardLikeCommentCountDataModel
import com.sooum.android.domain.model.DetailCommentCardDataModel
import com.sooum.android.domain.model.FeedCardDataModel
import com.sooum.android.domain.model.ImageUploadDataModel
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.domain.model.Status
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CardApi {
    @GET("/cards/home/latest")
    suspend fun getLatestCardList(
        @Header("Authorization") accessToken: String,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
    ): Response<SortedByLatestDataModel>

    @GET("/cards/home/popular")
    suspend fun getPopularityCardList(
        @Header("Authorization") accessToken: String,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
    ): Response<SortedByPopularityDataModel>

    @GET("/cards/home/distance")
    suspend fun getDistanceCardList(
        @Header("Authorization") accessToken: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("distanceFilter") distance: DistanceEnum,
    ): Response<SortedByDistanceDataModel>

    @GET("/cards/{cardId}/detail")
    suspend fun getFeedCard(
        @Header("Authorization") accessToken: String,
        @Path("cardId") cardId: Long,
    ): Response<FeedCardDataModel>

    @GET("/cards/current/{currentCardId}/summary")
    suspend fun getCardLikeCommentCount(
        @Header("Authorization") accessToken: String,
        @Path("currentCardId") cardId: Long,
    ): Response<DetailCardLikeCommentCountDataModel>

    @GET("/comments/current/{currentCardId}")
    suspend fun getDeatilCommentCard(
        @Header("Authorization") accessToken: String,
        @Path("currentCardId") cardId: Long,
    ): Response<DetailCommentCardDataModel>

    @POST("/cards/{cardId}/like")
    suspend fun likeOn(
        @Header("Authorization") accessToken: String,
        @Path("cardId") cardId: Long,
    ): Response<Status>

    @DELETE("/cards/{cardId}/like")
    suspend fun likeOff(
        @Header("Authorization") accessToken: String,
        @Path("cardId") cardId: Long,
    ): Response<Status>

    @GET("/imgs/default")
    suspend fun getDefaultImage(
        @Header("Authorization") accessToken: String,
        @Query("previousImgsName") previousImgsName: String? = null,
    ): Response<DefaultImageDataModel>

    @POST("/blocks")
    suspend fun userBlocks(
        @Header("Authorization") accessToken: String,
        @Body toMemberId: Long,
    ): Response<Status>

    @POST("/report/cards/{cardPk}")
    suspend fun cardReport(
        @Header("Authorization") accessToken: String,
        @Path("cardPk") cardPk: Long,
        @Query("reportType") reportTypeEnum : ReportTypeEnum
    )
    @DELETE("/cards/{cardId}")
    suspend fun deleteCard(
        @Header("Authorization") accessToken: String,
        @Path("cardId") cardId: Long,
    ): Response<Status>

    @GET("/imgs/cards/upload?extension=jpeg")
    suspend fun getImageUrl(
        @Header("Authorization") accessToken: String,
    ): Response<ImageUploadDataModel>
}