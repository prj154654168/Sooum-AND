package com.sooum.android.data.repository

import com.sooum.android.data.remote.TagAPI
import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.repository.TagRepository
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(private val tagApi: TagAPI) : TagRepository {
    override suspend fun getRecommendTag(): List<RecommendTagDataModel.Embedded.RecommendTag> {
        val response = tagApi.getRecommendTagList()

        var recommendTagList : List<RecommendTagDataModel.Embedded.RecommendTag> = listOf()

        if (response.isSuccessful) {
            recommendTagList = response.body()?.embedded?.recommendTagList ?: emptyList()
        }

        return  recommendTagList
    }
}