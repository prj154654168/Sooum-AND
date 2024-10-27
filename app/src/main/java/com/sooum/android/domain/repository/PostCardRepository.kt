package com.sooum.android.domain.repository

import com.sooum.android.domain.model.DefaultImageDataModel
import com.sooum.android.domain.model.RelatedTagDataModel

interface PostCardRepository {
    suspend fun getRelatedTag(keyword: String, size: Int) : List<RelatedTagDataModel.Embedded.RelatedTag>

    suspend fun getDefaultImage(previousImgsName: String?) : DefaultImageDataModel
}