package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.FollowerBody
import com.sooum.android.domain.model.FollowerDataModel
import com.sooum.android.domain.usecase.profile.DeleteFollowUseCase
import com.sooum.android.domain.usecase.profile.GetFollowerUserCase
import com.sooum.android.domain.usecase.profile.PostFollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowerViewModel @Inject constructor(
    private val followerUserCase: GetFollowerUserCase,
    private val postFollowUseCase: PostFollowUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase
) :
    ViewModel() {
    var followerList = mutableStateOf<List<FollowerDataModel.FollowerInfo>>(emptyList())
        private set

    init {
        getFollower()
    }

    fun getFollower() {
        viewModelScope.launch {
            try {
                followerList.value = emptyList()
                followerList.value = followerUserCase()._embedded.followerInfoList
            } catch (E: Exception) {
                println(E)
            }
        }
    }

    fun postFollow(userId: Long) {
        viewModelScope.launch {
            try {
                postFollowUseCase(FollowerBody(userId))
            } catch (E: Exception) {
                println(E)
            }
        }
    }
    fun deleteFollow(userId:Long){
        viewModelScope.launch {
            try {
                deleteFollowUseCase(userId)
            } catch (E: Exception) {
                println(E)
            }
        }
    }
}