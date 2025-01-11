package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.sooum.android.SooumApplication
import com.sooum.android.domain.model.NotificationDataModel
import com.sooum.android.domain.model.NotificationResponse
import com.sooum.android.domain.usecase.notification.AllReadNotificationUseCase
import com.sooum.android.domain.usecase.notification.AllUnreadCountUseCase
import com.sooum.android.domain.usecase.notification.AllUnreadNotificationUseCase
import com.sooum.android.domain.usecase.notification.CardReadNotificationUseCase
import com.sooum.android.domain.usecase.notification.CardUnreadCountUseCase
import com.sooum.android.domain.usecase.notification.CardUnreadNotificationUseCase
import com.sooum.android.domain.usecase.notification.LikeReadNotificationUseCase
import com.sooum.android.domain.usecase.notification.LikeUnreadCountUseCase
import com.sooum.android.domain.usecase.notification.LikeUnreadNotificationUseCase
import com.sooum.android.domain.usecase.notification.ReadNotificationUseCase
import com.sooum.android.enums.NotificationTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val allUnreadCountUseCase: AllUnreadCountUseCase,
    private val cardUnreadCountUseCase: CardUnreadCountUseCase,
    private val likeUnreadCountUseCase: LikeUnreadCountUseCase,
    allUnreadNotificationUseCase: AllUnreadNotificationUseCase,
    cardUnreadNotificationUseCase: CardUnreadNotificationUseCase,
    likeUnreadNotificationUseCase: LikeUnreadNotificationUseCase,
    allReadNotificationUseCase: AllReadNotificationUseCase,
    cardReadNotificationUseCase: CardReadNotificationUseCase,
    likeReadNotificationUseCase: LikeReadNotificationUseCase,
    private val readNotificationUseCase: ReadNotificationUseCase
): ViewModel() {
    var allUnreadCount = mutableStateOf(0)
        private set

    var cardUnreadCount = mutableStateOf(0)
        private set

    var likeUnreadCount = mutableStateOf(0)
        private set

    var allUnreadNotificationList = allUnreadNotificationUseCase().cachedIn(viewModelScope)

    val allReadNotificationList = allReadNotificationUseCase().cachedIn(viewModelScope)

    val likeUnreadNotificationList = likeUnreadNotificationUseCase().cachedIn(viewModelScope)

    val likeReadNotificationList = likeReadNotificationUseCase().cachedIn(viewModelScope)

    val cardUnreadNotificationList = cardUnreadNotificationUseCase().cachedIn(viewModelScope)

    val cardReadNotificationList = cardReadNotificationUseCase().cachedIn(viewModelScope)

    init {
        getAllUnreadCount()
        getCardUnreadCount()
        getLikeUnreadCount()

        SooumApplication().removeVariable("notificationId")
        SooumApplication().removeVariable("targetCardId")
    }

    fun getAllUnreadCount() {
        viewModelScope.launch {
            val result = allUnreadCountUseCase()
            allUnreadCount.value = result
        }
    }

    fun getCardUnreadCount() {
        viewModelScope.launch {
            val result = cardUnreadCountUseCase()
            cardUnreadCount.value = result
        }
    }

    fun getLikeUnreadCount() {
        viewModelScope.launch {
            val result = likeUnreadCountUseCase()
            likeUnreadCount.value = result
        }
    }

    fun handleNotificationRead(notificationId: Long) {
        viewModelScope.launch {
            val result = readNotificationUseCase(notificationId)
        }
    }

    fun Flow<PagingData<NotificationDataModel>>.removeItem(notificationId: Long): Flow<PagingData<NotificationDataModel>> {
        return this.map { value: PagingData<NotificationDataModel> ->
            value.filter { item ->
                item.notificationId != notificationId
            }
        }
    }

    fun removeWaringNotification(notificationId: Long) {
        allUnreadNotificationList = allUnreadNotificationList.removeItem(notificationId)
    }
}