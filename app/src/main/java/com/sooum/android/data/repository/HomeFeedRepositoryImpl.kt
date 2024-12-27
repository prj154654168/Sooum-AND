package com.sooum.android.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.paging.DistanceFeedPagingSource
import com.sooum.android.data.paging.LatestFeedPagingSource
import com.sooum.android.data.paging.PopularityFeedPagingSource
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.domain.repository.HomeFeedRepository
import com.sooum.android.enums.DistanceEnum
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeFeedRepositoryImpl @Inject constructor(private val cardApi: CardApi) :
    HomeFeedRepository {
    override fun getLatestCardList(
        latitude: Double?,
        longitude: Double?,
    ): Flow<PagingData<SortedByLatestDataModel.Embedded.LatestFeedCard>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { LatestFeedPagingSource(cardApi, latitude, longitude) }
        ).flow
    }

    override fun getPopularityCardList(
        latitude: Double?,
        longitude: Double?,
    ): Flow<PagingData<SortedByPopularityDataModel.Embedded.PopularFeedCard>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PopularityFeedPagingSource(cardApi, latitude, longitude) }
        ).flow
    }

    override fun getDistanceCardList(
        latitude: Double,
        longitude: Double,
        distanceFilter: DistanceEnum,
    ): Flow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>> {
        return Pager(
            config = PagingConfig(
                pageSize = 50,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DistanceFeedPagingSource(cardApi, latitude, longitude, distanceFilter) }
        ).flow
    }
}