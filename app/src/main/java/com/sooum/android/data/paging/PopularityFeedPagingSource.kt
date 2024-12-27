package com.sooum.android.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.SortedByPopularityDataModel

class PopularityFeedPagingSource(
    private val apiService: CardApi,
    private val latitude: Double?,
    private val longitude: Double?
) : PagingSource<Long, SortedByPopularityDataModel.Embedded.PopularFeedCard>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, SortedByPopularityDataModel.Embedded.PopularFeedCard> {
        return try {
            val lastCardId = params.key
            val response = if (lastCardId == null) {
                apiService.getPopularityCardList(latitude, longitude)
            } else {
                apiService.getPopularityCardListAfter(lastCardId, latitude, longitude)
            }

            if (response.isSuccessful) {
                // 204 No Content 처리
                if (response.code() == 204) {
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null // 데이터가 없으므로 더 이상 로드하지 않음
                    )
                }

                val body = response.body() ?: return LoadResult.Error(Exception("Empty response body"))
                val cards = body.embedded.popularCardRetrieveList

                LoadResult.Page(
                    data = cards,
                    prevKey = null, // Always null as we're only paging forward
                    nextKey = cards.lastOrNull()?.id // 마지막 카드의 ID를 다음 키로 사용
                )
            } else {
                LoadResult.Error(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, SortedByPopularityDataModel.Embedded.PopularFeedCard>): Long? {
        return null
    }
}
