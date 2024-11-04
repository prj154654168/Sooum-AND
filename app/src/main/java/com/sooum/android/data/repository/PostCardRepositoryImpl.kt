package com.sooum.android.data.repository

import android.util.Log
import com.sooum.android.Constants.ACCESS_TOKEN
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.TagAPI
import com.sooum.android.domain.model.DefaultImageDataModel
import com.sooum.android.domain.model.RelatedTagDataModel
import com.sooum.android.domain.repository.PostCardRepository
import javax.inject.Inject

class PostCardRepositoryImpl @Inject constructor(private val tagApi: TagAPI, private val cardApi: CardApi) : PostCardRepository {
    override suspend fun getRelatedTag(
        keyword: String,
        size: Int
    ): List<RelatedTagDataModel.Embedded.RelatedTag> {
        val response = tagApi.getRelatedTag(ACCESS_TOKEN, keyword, size)

        var relatedTagList : List<RelatedTagDataModel.Embedded.RelatedTag> = listOf()

        if (response.isSuccessful) {
            relatedTagList = response.body()?.embedded?.relatedTagList ?: emptyList()
        }
        else {
            Log.d("AddPostRepository", "getRelatedTagList Failed")
        }
        return relatedTagList
    }

    override suspend fun getDefaultImage(previousImgsName: String?): DefaultImageDataModel {
        val response = cardApi.getDefaultImage(ACCESS_TOKEN, previousImgsName)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            Log.d("AddPostRepository", "getDefaultImageList Failed")
            throw Exception("Failed to get default image") // 실패 시 예외 처리
        }
    }
}