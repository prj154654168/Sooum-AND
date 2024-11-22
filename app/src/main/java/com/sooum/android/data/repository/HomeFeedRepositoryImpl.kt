package com.sooum.android.data.repository

import android.util.Log
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.domain.repository.HomeFeedRepository
import com.sooum.android.enums.DistanceEnum
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeFeedRepositoryImpl @Inject constructor(private val cardApi: CardApi) :
    HomeFeedRepository {
    override suspend fun getLatestCardList(
        latitude: Double?,
        longitude: Double?,
    ): List<SortedByLatestDataModel.Embedded.LatestFeedCard> {
        val response = cardApi.getLatestCardList(latitude, longitude)

        var latestCardList: List<SortedByLatestDataModel.Embedded.LatestFeedCard> = listOf()

        if (response.isSuccessful) {
            Log.d("MainActivity", "latestReqSuccess")
            latestCardList = response.body()?.embedded?.latestFeedCardDtoList ?: emptyList()
            Log.d("CardList", latestCardList.size.toString())
        } else {
            Log.d("MainActivity", "getLatestCardList fail")
        }

        return latestCardList
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

    override suspend fun getDistanceCardList(
        latitude: Double,
        longitude: Double,
        distanceFilter: DistanceEnum,
    ): List<SortedByDistanceDataModel.Embedded.DistanceFeedCard> {
        val response =
            cardApi.getDistanceCardList(latitude, longitude, distanceFilter)

        var distanceCardList: List<SortedByDistanceDataModel.Embedded.DistanceFeedCard> = listOf()

        if (response.isSuccessful) {
            Log.d("MainActivity", "latestReqSuccess")
            distanceCardList = response.body()?.embedded?.distanceCardDtoList ?: emptyList()
        } else {
            Log.d("MainActivity", "getLatestCardList fail")
        }

        return distanceCardList
    }
}