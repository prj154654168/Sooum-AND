package com.sooum.android.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.enums.DistanceEnum
import kotlinx.coroutines.delay
import retrofit2.HttpException

class DistanceFeedPagingSource(
    private val apiService: CardApi,
    private val latitude: Double,
    private val longitude: Double,
    private val distanceEnum: DistanceEnum
) : PagingSource<Long, SortedByDistanceDataModel.Embedded.DistanceFeedCard>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, SortedByDistanceDataModel.Embedded.DistanceFeedCard> {
        val maxRetries = 3
        var currentRetry = 0

        while (true) {
            try {
                val lastCardId = params.key
                val response = if (lastCardId == null) {
                    // 초기 요청
                    apiService.getDistanceCardList(latitude, longitude, distanceEnum)
                } else {
                    // 추가 요청
                    apiService.getDistanceCardListAfter(lastCardId, latitude, longitude, distanceEnum)
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
                    val cards = body.embedded.distanceCardDtoList

                    return LoadResult.Page(
                        data = cards,
                        prevKey = null,
                        nextKey = cards.lastOrNull()?.id
                    )
                } else {
                    // 429 에러인 경우 재시도 처리
                    if (response.code() == 429 && currentRetry < maxRetries) {
                        currentRetry++
                        Log.e("DistanceFeedPagingSource", "429 Too Many Requests, retry attempt: $currentRetry")
                        delay(500L * currentRetry) // 지수 백오프 적용
                        continue
                    }
                    return LoadResult.Error(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                // 예외가 HttpException이고 429 에러인 경우 재시도 처리
                if (e is HttpException && e.code() == 429 && currentRetry < maxRetries) {
                    currentRetry++
                    Log.e("DistanceFeedPagingSource", "Exception 429 Too Many Requests, retry attempt: $currentRetry")
                    delay(500L * currentRetry)
                    continue
                }
                return LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Long, SortedByDistanceDataModel.Embedded.DistanceFeedCard>): Long? {
        // refresh 시 항상 초기 요청을 보장
        return null
    }
}
