package com.sooum.android.domain.repository

import com.sooum.android.domain.model.DefaultImageDataModel
import com.sooum.android.domain.model.RelatedTagDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.enums.FontEnum
import com.sooum.android.enums.ImgTypeEnum
import retrofit2.http.Body

interface PostCardRepository {
    suspend fun getRelatedTag(keyword: String, size: Int) : List<RelatedTagDataModel.Embedded.RelatedTag>

    suspend fun getDefaultImage(previousImgsName: String?) : DefaultImageDataModel

    suspend fun postFeedCard(isDistanceShared: Boolean,
                             latitude: Double?,
                             longitude: Double?,
                             isPublic: Boolean,
                             isStory: Boolean,
                             content: String,
                             font: FontEnum,
                             imgType: ImgTypeEnum,
                             imgName: String,
                             feedTags: List<String>) : Status
}