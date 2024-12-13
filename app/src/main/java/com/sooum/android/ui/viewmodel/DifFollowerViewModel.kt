package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.FollowerBody
import com.sooum.android.domain.model.FollowerDataModel
import com.sooum.android.domain.usecase.profile.DeleteFollowUseCase
import com.sooum.android.domain.usecase.profile.DifFollowerUseCase
import com.sooum.android.domain.usecase.profile.PostFollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class DifFollowerViewModel @Inject constructor(
    private val difFollowerUseCase: DifFollowerUseCase,
    private val postFollowUseCase: PostFollowUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
) :
    ViewModel() {
    var followerList = mutableStateOf<List<FollowerDataModel.FollowerInfo>>(emptyList())
        private set

    fun getFollower(profileOwnerPk: Long) {
        viewModelScope.launch {
            try {
                followerList.value = emptyList()
                followerList.value = difFollowerUseCase(profileOwnerPk)._embedded.followerInfoList
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