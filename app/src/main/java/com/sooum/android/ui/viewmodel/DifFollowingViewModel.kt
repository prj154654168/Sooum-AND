package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.FollowerBody
import com.sooum.android.domain.model.FollowingDataModel
import com.sooum.android.domain.usecase.profile.DeleteFollowUseCase
import com.sooum.android.domain.usecase.profile.DifFollowingUseCase
import com.sooum.android.domain.usecase.profile.PostFollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class DifFollowingViewModel @Inject constructor(
    private val difFollowingUseCase: DifFollowingUseCase,
    private val postFollowUseCase: PostFollowUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
) :
    ViewModel() {
    var followingList = mutableStateOf<List<FollowingDataModel.FollowingInfo>>(emptyList())
        private set

    fun getFollowing(profileOwnerPk: Long) {
        viewModelScope.launch {
            try {
                followingList.value = emptyList()
                followingList.value =
                    difFollowingUseCase(profileOwnerPk)._embedded.followingInfoList
                Log.e("followingList", followingList.toString())
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