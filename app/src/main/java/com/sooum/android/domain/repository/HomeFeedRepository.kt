package com.sooum.android.domain.repository

import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.enums.DistanceEnum

interface HomeFeedRepository {
    suspend fun getLatestCardList(latitude: Double?, longitude: Double?) : List<SortedByLatestDataModel.Embedded.LatestFeedCard>

    suspend fun getPopularityCardList(latitude: Double?, longitude: Double?) : List<SortedByPopularityDataModel.Embedded.PopularFeedCard>

    suspend fun getDistanceCardList(latitude: Double, longitude: Double, distanceFilter: DistanceEnum) : List<SortedByDistanceDataModel.Embedded.DistanceFeedCard>
}