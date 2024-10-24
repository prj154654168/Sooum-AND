package com.sooum.android

import com.sooum.android.model.RelatedTagDataModel
import com.sooum.android.model.SortedByLatestDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TagAPI {
    @GET("/tags/search")
    suspend fun getRelatedTag(
        @Header("Authorization") accessToken: String,
        @Query("keyword") keyword: String,
        @Query("size") size: Int
    ): Response<RelatedTagDataModel>
}