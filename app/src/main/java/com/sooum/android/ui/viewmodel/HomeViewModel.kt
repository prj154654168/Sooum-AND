package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.domain.usecase.homefeed.DistanceFeedUseCase
import com.sooum.android.domain.usecase.homefeed.LatestFeedUseCase
import com.sooum.android.domain.usecase.homefeed.PopularityFeedUseCase
import com.sooum.android.enums.DistanceEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLatestFeedUseCase: LatestFeedUseCase,
    private val getPopularityFeedUseCase: PopularityFeedUseCase,
    private val getDistanceFeedUseCase: DistanceFeedUseCase
): ViewModel() {
    var latestCardList = mutableStateListOf<SortedByLatestDataModel.Embedded.LatestFeedCard>()
        private set

    var popularityCardList = mutableStateListOf<SortedByPopularityDataModel.Embedded.PopularFeedCard>()
        private set

    var distance1CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    var distance5CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    var distance10CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    var distance20CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    var distance50CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    fun fetchLatestCardList(latitude: Double?, longitude: Double?, onFetchFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                val cardList = getLatestFeedUseCase(latitude, longitude)
                latestCardList.clear()
                latestCardList.addAll(cardList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
            finally {
                delay(500)
                onFetchFinished()
            }
        }
    }

    fun fetchPopularityCardList(latitude: Double?, longitude: Double?, onFetchFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                val cardList = getPopularityFeedUseCase(latitude, longitude)
                popularityCardList.clear()
                popularityCardList.addAll(cardList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
            finally {
                delay(500)
                onFetchFinished()
            }
        }
    }

    fun fetchDistance1CardList(latitude: Double, longitude: Double, onFetchFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                val cardList = getDistanceFeedUseCase(latitude, longitude, DistanceEnum.UNDER_1)
                distance1CardList.clear()
                distance1CardList.addAll(cardList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
            finally {
                delay(500)
                onFetchFinished()
            }
        }
    }

    fun fetchDistance5CardList(latitude: Double, longitude: Double, onFetchFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                val cardList = getDistanceFeedUseCase(latitude, longitude, DistanceEnum.UNDER_5)
                distance5CardList.clear()
                distance5CardList.addAll(cardList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
            finally {
                delay(500)
                onFetchFinished()
            }
        }
    }

    fun fetchDistance10CardList(latitude: Double, longitude: Double, onFetchFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                val cardList = getDistanceFeedUseCase(latitude, longitude, DistanceEnum.UNDER_10)
                distance10CardList.clear()
                distance10CardList.addAll(cardList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
            finally {
                delay(500)
                onFetchFinished()
            }
        }
    }

    fun fetchDistance20CardList(latitude: Double, longitude: Double, onFetchFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                val cardList = getDistanceFeedUseCase(latitude, longitude, DistanceEnum.UNDER_20)
                distance20CardList.clear()
                distance20CardList.addAll(cardList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
            finally {
                delay(500)
                onFetchFinished()
            }
        }
    }

    fun fetchDistance50CardList(latitude: Double, longitude: Double, onFetchFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                val cardList = getDistanceFeedUseCase(latitude, longitude, DistanceEnum.UNDER_50)
                distance50CardList.clear()
                distance50CardList.addAll(cardList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
            finally {
                delay(500)
                onFetchFinished()
            }
        }
    }
}
