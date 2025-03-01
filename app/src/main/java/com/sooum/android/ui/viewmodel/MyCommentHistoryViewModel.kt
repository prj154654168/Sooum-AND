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
) : ViewModel() {

    // 댓글 데이터 상태
    var myCommentCard = mutableStateOf<List<MyCommentCardDataModel.MyCommentCardDto>>(emptyList())
        private set

    // 새로 고침 상태 관리
    var refreshing = mutableStateOf(false)
        private set

    init {
        // 초기 댓글 데이터 가져오기
        getMyCommentCard()
    }

    // 댓글 데이터 새로고침
    fun getMyCommentCard() {
        viewModelScope.launch {
            // 새로 고침 시작
            refreshing.value = true
            // runCatching을 사용하여 예외를 처리
            runCatching {
                // 데이터 가져오기
                myCommentCard.value = myCommentUseCase()
            }.onFailure {
                // 예외 처리
            }.onSuccess {
                refreshing.value = false
            }
        }
    }

    // 새로 고침 함수
    fun refreshComments() {
        getMyCommentCard()
    }
}
