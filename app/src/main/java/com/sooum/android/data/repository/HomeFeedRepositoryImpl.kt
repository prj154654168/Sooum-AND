package com.sooum.android.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.paging.DistanceFeedPagingSource
import com.sooum.android.data.paging.LatestFeedPagingSource
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
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { LatestFeedPagingSource(cardApi, latitude, longitude) }
        ).flow
    }

    override suspend fun getPopularityCardList(
        latitude: Double?,
        longitude: Double?,
    ): List<SortedByPopularityDataModel.Embedded.PopularFeedCard> {
        val response = cardApi.getPopularityCardList(latitude, longitude)
        var popularityCardList: List<SortedByPopularityDataModel.Embedded.PopularFeedCard> =
            listOf()
        if (response.isSuccessful) {
            Log.d("MainActivity", "latestReqSuccess")
            popularityCardList = response.body()?.embedded?.popularCardRetrieveList ?: emptyList()
        } else {
            Log.d("MainActivity", "getLatestCardList fail")
        }
        return popularityCardList
    }


    override fun getDistanceCardList(
        latitude: Double,
        longitude: Double,
        distanceFilter: DistanceEnum,
    ): Flow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DistanceFeedPagingSource(cardApi, latitude, longitude, distanceFilter) }
        ).flow
    }
}