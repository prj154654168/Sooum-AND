package com.sooum.android.data.repository

import android.util.Log
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.TagAPI
import com.sooum.android.domain.model.DefaultImageDataModel
import com.sooum.android.domain.model.PostFeedRequestDataModel
import com.sooum.android.domain.model.RelatedTagDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.repository.PostCardRepository
import com.sooum.android.enums.FontEnum
import com.sooum.android.enums.ImgTypeEnum
import javax.inject.Inject

class PostCardRepositoryImpl @Inject constructor(
    private val tagApi: TagAPI,
    private val cardApi: CardApi,
) : PostCardRepository {
    override suspend fun getRelatedTag(
        keyword: String,
        size: Int,
    ): List<RelatedTagDataModel.Embedded.RelatedTag> {
        val response = tagApi.getRelatedTag(keyword, size)

        var relatedTagList: List<RelatedTagDataModel.Embedded.RelatedTag> = listOf()

        if (response.isSuccessful) {
            relatedTagList = response.body()?.embedded?.relatedTagList ?: emptyList()
        } else {
            Log.d("AddPostRepository", "getRelatedTagList Failed")
        }
        return relatedTagList
    }

    override suspend fun getDefaultImage(previousImgsName: String?): DefaultImageDataModel {
        val response = cardApi.getDefaultImage(previousImgsName)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"

            throw Exception("Failed to get default image: $errorMessage")
        }

    }

    override suspend fun postFeedCard(
        isDistanceShared: Boolean,
        latitude: Double?,
        longitude: Double?,
        isPublic: Boolean,
        isStory: Boolean,
        content: String,
        font: FontEnum,
        imgType: ImgTypeEnum,
        imgName: String,
        feedTags: List<String>?,
    ): Status {
        val response = cardApi.postFeedCard(

            PostFeedRequestDataModel(
                isDistanceShared,
                latitude,
                longitude,
                isPublic,
                isStory,
                content,
                font,
                imgType,
                imgName,
                feedTags
            )
        )
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }
}