package com.sooum.android.domain.repository

import com.sooum.android.domain.model.RecommendTagDataModel

interface TagRepository {
    suspend fun getRecommendTag() : List<RecommendTagDataModel.Embedded.RecommendTag>
}