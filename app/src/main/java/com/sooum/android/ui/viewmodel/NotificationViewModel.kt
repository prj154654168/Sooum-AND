package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.usecase.notification.AllUnreadCountUseCase
import com.sooum.android.domain.usecase.notification.CardUnreadCountUseCase
import com.sooum.android.domain.usecase.notification.LikeUnreadCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val allUnreadCountUseCase: AllUnreadCountUseCase,
    private val cardUnreadCountUseCase: CardUnreadCountUseCase,
    private val likeUnreadCountUseCase: LikeUnreadCountUseCase
): ViewModel() {
    var allUnreadCount = mutableStateOf(0)
        private set

    var cardUnreadCount = mutableStateOf(0)
        private set

    var likeUnreadCount = mutableStateOf(0)
        private set

    init {
        getAllUnreadCount()
        getCardUnreadCount()
        getLikeUnreadCount()
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
}