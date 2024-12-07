package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.MyCommentCardDataModel
import com.sooum.android.domain.usecase.profile.MyCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MyCommentHistoryViewModel @Inject constructor(
    private val myCommentUseCase: MyCommentUseCase,
) :
    ViewModel() {
    var myCommentCard = mutableStateOf<List<MyCommentCardDataModel.MyCommentCardDto>>(emptyList())
        private set

    init {
        getMyCommentCard()
    }

    fun getMyCommentCard() {
        viewModelScope.launch {
            myCommentCard.value = myCommentUseCase()
        }
    }
}