package com.sooum.android.domain.repository

import androidx.paging.PagingData
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.enums.DistanceEnum
import kotlinx.coroutines.flow.Flow

interface HomeFeedRepository {
    fun getLatestCardList(latitude: Double?, longitude: Double?) : Flow<PagingData<SortedByLatestDataModel.Embedded.LatestFeedCard>>

    suspend fun getPopularityCardList(latitude: Double?, longitude: Double?) : List<SortedByPopularityDataModel.Embedded.PopularFeedCard>

    fun getDistanceCardList(latitude: Double, longitude: Double, distanceFilter: DistanceEnum) : Flow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>
}