package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.NoticeDataModel
import com.sooum.android.domain.usecase.profile.NoticeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val noticeUseCase: NoticeUseCase,
) :
    ViewModel() {
    var noticeList = mutableStateOf<List<NoticeDataModel.NoticeDto>>(emptyList())
        private set

    init {
        getNotice()
    }

    fun getNotice() {
        viewModelScope.launch {
            noticeList.value = noticeUseCase()
        }
    }
}