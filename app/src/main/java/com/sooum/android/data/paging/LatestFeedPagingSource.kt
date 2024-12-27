package com.sooum.android.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.SortedByLatestDataModel

class LatestFeedPagingSource(
    private val apiService: CardApi,
    private val latitude: Double?,
    private val longitude: Double?
) : PagingSource<Long, SortedByLatestDataModel.Embedded.LatestFeedCard>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, SortedByLatestDataModel.Embedded.LatestFeedCard> {
        return try {
            val lastCardId = params.key
            val response = if (lastCardId == null) {
                // 초기 요청
                apiService.getLatestCardList(latitude, longitude)
            } else {
                // 추가 요청
                apiService.getLatestCardListAfter(lastCardId, latitude, longitude)
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
                val cards = body.embedded.latestFeedCardDtoList

                LoadResult.Page(
                    data = cards,
                    prevKey = null,
                    nextKey = cards.lastOrNull()?.id
                )
            } else {
                LoadResult.Error(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, SortedByLatestDataModel.Embedded.LatestFeedCard>): Long? {
        // refresh() 시 항상 초기 요청을 보장
        return null
    }
}
