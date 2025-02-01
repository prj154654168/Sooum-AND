package com.sooum.android.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.TagFeedDataModel

class TagFeedPagingSource(
    private val apiService: CardApi,
    private val tagId: String,
    private val latitude: Double?,
    private val longitude: Double?
) : PagingSource<String, TagFeedDataModel.Embedded.TagFeedCardDto>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, TagFeedDataModel.Embedded.TagFeedCardDto> {
        return try {
            val lastCardId = params.key
            val response = if (lastCardId == null) {
                // 초기 요청
                apiService.getTagFeed(tagId, latitude, longitude)
            } else {
                // 추가 요청
                apiService.getTagFeedAfter(tagId, latitude, longitude, lastCardId)
            }

            if (response.isSuccessful) {
                if (response.code() == 204) {
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                }

                val body = response.body() ?: return LoadResult.Error(Exception("Empty response body"))
                val cards = body._embedded.tagFeedCardDtoList

                val nextKey = if (cards.size < 50) null else cards.lastOrNull()?.id

                LoadResult.Page(
                    data = cards,
                    prevKey = null,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, TagFeedDataModel.Embedded.TagFeedCardDto>): String? {
        // refresh() 시 항상 초기 요청을 보장
        return null
    }
}
