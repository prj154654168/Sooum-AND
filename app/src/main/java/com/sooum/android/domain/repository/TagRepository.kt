package com.sooum.android.domain.repository

import com.sooum.android.domain.model.FavoriteTagDataModel
import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.SearchTagDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.model.TagFeedDataModel
import com.sooum.android.domain.model.TagSummaryDataModel

interface TagRepository {
    suspend fun getRecommendTag() : List<RecommendTagDataModel.Embedded.RecommendTag>

    suspend fun postTagFavorite(tagId: String) : Status

    suspend fun deleteTagFavorite(tagId: String) : Status

    suspend fun getTagSummary(tagId: String) : TagSummaryDataModel

    suspend fun getFavoriteTag(last: String?) : FavoriteTagDataModel

    suspend fun getTagFeedList(tagId: String, latitude: Double?, longitude: Double?, laskPk: Long?): TagFeedDataModel

    suspend fun getSearchTag(keyword: String): SearchTagDataModel
}