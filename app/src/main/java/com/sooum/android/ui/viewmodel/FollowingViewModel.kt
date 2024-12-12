package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.FollowerBody
import com.sooum.android.domain.model.FollowingDataModel
import com.sooum.android.domain.usecase.profile.DeleteFollowUseCase
import com.sooum.android.domain.usecase.profile.GetFollowingUserCase
import com.sooum.android.domain.usecase.profile.PostFollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val followingUserCase: GetFollowingUserCase,
    private val postFollowUseCase: PostFollowUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
) :
    ViewModel() {
    var followingList = mutableStateOf<List<FollowingDataModel.FollowingInfo>>(emptyList())
        private set

    init {
        getFollowing()
    }

    fun getFollowing() {
        viewModelScope.launch {
            try {
                followingList.value= emptyList()
                followingList.value = followingUserCase()._embedded.followingInfoList
                Log.e("followingList",followingList.toString())
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

    fun deleteFollow(userId: Long) {
        viewModelScope.launch {
            try {
                deleteFollowUseCase(userId)
            } catch (E: Exception) {
                println(E)
            }
        }
    }
}