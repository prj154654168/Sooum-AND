package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
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
    getPopularityFeedUseCase: PopularityFeedUseCase,
    getDistanceFeedUseCase: DistanceFeedUseCase
): ViewModel() {
    val lazyLatestFeed = getLatestFeedUseCase(User.userInfo.latitude, User.userInfo.longitude).cachedIn(viewModelScope)

    val lazyPopularityFeed = getPopularityFeedUseCase(User.userInfo.latitude, User.userInfo.longitude).cachedIn(viewModelScope)

    val lazyDistance1Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_1).cachedIn(viewModelScope)
    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)

    val lazyDistance5Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_5).cachedIn(viewModelScope)
    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)

    val lazyDistance10Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_10).cachedIn(viewModelScope)
    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)

    val lazyDistance20Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_20).cachedIn(viewModelScope)
    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)

    val lazyDistance50Feed = if (User.userInfo.latitude != null && User.userInfo.longitude != null) getDistanceFeedUseCase(User.userInfo.latitude!!, User.userInfo.longitude!!, DistanceEnum.UNDER_50).cachedIn(viewModelScope)
    else emptyFlow<PagingData<SortedByDistanceDataModel.Embedded.DistanceFeedCard>>().cachedIn(viewModelScope)
}
