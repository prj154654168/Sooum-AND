package com.sooum.android.domain.repository

import androidx.paging.PagingData
import com.sooum.android.domain.model.FavoriteTagDataModel
import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.SearchTagDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.model.TagFeedDataModel
import com.sooum.android.domain.model.TagSummaryDataModel
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun getRecommendTag() : List<RecommendTagDataModel.Embedded.RecommendTag>

    suspend fun postTagFavorite(tagId: String) : Status

    suspend fun deleteTagFavorite(tagId: String) : Status

    suspend fun getTagSummary(tagId: String) : TagSummaryDataModel

    suspend fun getFavoriteTag(last: String?) : FavoriteTagDataModel

    fun getTagFeedList(tagId: String, latitude: Double?, longitude: Double?): Flow<PagingData<TagFeedDataModel.Embedded.TagFeedCardDto>>

    suspend fun getSearchTag(keyword: String): SearchTagDataModel
}