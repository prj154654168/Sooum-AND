package com.sooum.android.data.remote

import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.RelatedTagDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TagAPI {
    @GET("/tags/search")
    suspend fun getRelatedTag(

        @Query("keyword") keyword: String,
        @Query("size") size: Int
    ): Response<RelatedTagDataModel>

    @GET("/tags/recommendation")
    suspend fun getRecommendTagList() : Response<RecommendTagDataModel>
}