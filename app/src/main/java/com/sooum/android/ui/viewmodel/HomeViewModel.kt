package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.sooum.android.User
import com.sooum.android.domain.model.SortedByDistanceDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.SortedByPopularityDataModel
import com.sooum.android.domain.usecase.homefeed.DistanceFeedUseCase
import com.sooum.android.domain.usecase.homefeed.LatestFeedUseCase
import com.sooum.android.domain.usecase.homefeed.PopularityFeedUseCase
import com.sooum.android.domain.usecase.notification.AllUnreadCountUseCase
import com.sooum.android.enums.DistanceEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getLatestFeedUseCase: LatestFeedUseCase,
    private val getPopularityFeedUseCase: PopularityFeedUseCase,
    private val getDistanceFeedUseCase: DistanceFeedUseCase,
    private val getAllUnreadCountUseCase: AllUnreadCountUseCase,
): ViewModel() {

    val lazyLatestFeed = getLatestFeedUseCase(User.userInfo.latitude, User.userInfo.longitude).cachedIn(viewModelScope)

    var popularityCardList = mutableStateListOf<SortedByPopularityDataModel.Embedded.PopularFeedCard>()
        private set

//    val lazyDistance1Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_1).cachedIn(viewModelScope)
//    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)
//
//    val lazyDistance5Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_5).cachedIn(viewModelScope)
//    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)
//
//    val lazyDistance10Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_10).cachedIn(viewModelScope)
//    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)
//
//    val lazyDistance20Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_20).cachedIn(viewModelScope)
//    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)
//
//    val lazyDistance50Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_50).cachedIn(viewModelScope)
//    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)



    // 기존에 5개의 LazyFlow를 미리 생성하던 방식을 제거하고,
    // 선택된 거리(distance)에 따라 해당 Flow를 반환하는 함수로 변경함.
    fun getLazyDistanceFeed(distance: DistanceEnum): Flow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>> {
        return if (User.userInfo.latitude != null && User.userInfo.longitude != null) {
            getDistanceFeedUseCase(
                User.userInfo.latitude!!,
                User.userInfo.longitude!!,
                distance
            ).cachedIn(viewModelScope)
        } else {
            emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>()
                .cachedIn(viewModelScope)
        }
    }


    var unreadNotificationCount = mutableStateOf(0)
        private set

    fun fetchPopularityCardList(latitude: Double?, longitude: Double?, onFetchFinished: () -> Unit) {
        viewModelScope.launch {
            try {
                delay(500)
                val cardList = getPopularityFeedUseCase(latitude, longitude)
                popularityCardList.clear()
                popularityCardList.addAll(cardList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
            finally {
                onFetchFinished()
            }
        }
    }

    fun fetchUnreadNotificationCount() {
        viewModelScope.launch {
            try {
                delay(500)
                val unreadCount = getAllUnreadCountUseCase()
                unreadNotificationCount.value = unreadCount
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
        }
    }
}
