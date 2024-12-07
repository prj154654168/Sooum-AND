package com.sooum.android.data.repository

import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.TagAPI
import com.sooum.android.domain.model.FavoriteTagDataModel
import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.model.TagFeedDataModel
import com.sooum.android.domain.model.TagSummaryDataModel
import com.sooum.android.domain.repository.TagRepository
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(private val tagApi: TagAPI, private val cardApi: CardApi) : TagRepository {
    override suspend fun getRecommendTag(): List<RecommendTagDataModel.Embedded.RecommendTag> {
        val response = tagApi.getRecommendTagList()

        var recommendTagList : List<RecommendTagDataModel.Embedded.RecommendTag> = listOf()

        if (response.isSuccessful) {
            recommendTagList = response.body()?.embedded?.recommendTagList ?: emptyList()
        }

        return  recommendTagList
    }

    override suspend fun postTagFavorite(tagId: String): Status {
        val response = tagApi.postTagFavorite(tagId)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun deleteTagFavorite(tagId: String): Status {
        val response = tagApi.deleteTagFavorite(tagId)

        if (response.isSuccessful) {
            if (response.code() == 204) {
                return Status(204, "Good~!", "Delete Favorite Tag Successfully")
            }
            else {
                return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
            }
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun getTagSummary(tagId: String): TagSummaryDataModel {
        val response = tagApi.getTagSummary(tagId)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun getFavoriteTag(last: String?): FavoriteTagDataModel {
        val response = tagApi.getFavoriteTag()

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun getTagFeedList(
        tagId: String,
        latitude: Double?,
        longitude: Double?,
        laskPk: Long?
    ): TagFeedDataModel {
        val response = cardApi.getTagFeed(tagId, latitude, longitude, laskPk)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }
}