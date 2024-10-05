package com.sooum.android

import com.sooum.android.enums.DistanceEnum
import com.sooum.android.model.SortedByDistanceDataModel
import com.sooum.android.model.SortedByLatestDataModel
import com.sooum.android.model.SortedByPopularityDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CardApi {
    @GET("/cards/home/latest")
    suspend fun getLatestCardList(@Header("Authorization") accessToken: String, @Query("latitude") latitude: Double? = null, @Query("longitude") longitude: Double? = null) : Response<SortedByLatestDataModel>

    @GET("/cards/home/popular")
    suspend fun getPopularityCardList(@Header("Authorization") accessToken : String, @Query("latitude") latitude: Double? = null, @Query("longitude") longitude: Double? = null) : Response<SortedByPopularityDataModel>

    @GET("/cards/home/distance")
    suspend fun getDistanceCardList(@Header("Authorization") accessToken: String, @Query("latitude") latitude: Double, @Query("longitude") longitude: Double, @Query("distanceFilter") distance : DistanceEnum)  : Response<SortedByDistanceDataModel>
}