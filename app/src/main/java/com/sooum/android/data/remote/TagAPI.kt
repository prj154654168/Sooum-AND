package com.sooum.android.data.remote

import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.RelatedTagDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.model.TagSummaryDataModel
import com.sooum.android.enums.ReportTypeEnum
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TagAPI {
    @GET("/tags/search")
    suspend fun getRelatedTag(

        @Query("keyword") keyword: String,
        @Query("size") size: Int
    ): Response<RelatedTagDataModel>

    @GET("/tags/recommendation")
    suspend fun getRecommendTagList() : Response<RecommendTagDataModel>

    @POST("/tags/{tagId}/favorite")
    suspend fun postTagFavorite(
        @Path("tagId") tagId: String
    ): Response<Status>

    @DELETE("/tags/{tagId}/favorite")
    suspend fun deleteTagFavorite(
        @Path("tagId") tagId: String
    ): Response<Status>

    @GET("/tags/{tagId}/summary")
    suspend fun getTagSummary(
        @Path("tagId") tagId: String
    ) : Response<TagSummaryDataModel>
}