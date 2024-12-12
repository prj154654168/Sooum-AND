package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.SooumApplication
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.BlockBody
import com.sooum.android.domain.model.DifProfileDataModel
import com.sooum.android.domain.model.FollowerBody
import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.usecase.profile.DeleteFollowUseCase
import com.sooum.android.domain.usecase.profile.DifFeedCardUseCase
import com.sooum.android.domain.usecase.profile.DifProfileUseCase
import com.sooum.android.domain.usecase.profile.PostFollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DifProfileViewModel @Inject constructor(
    private val difProfileUseCase: DifProfileUseCase,
    private val difFeedCardUseCase: DifFeedCardUseCase,
    private val postFollowUseCase: PostFollowUseCase,
    private val deleteFollowUseCase: DeleteFollowUseCase,
) :
    ViewModel() {
    var difProfile = mutableStateOf<DifProfileDataModel?>(null)
        private set
    var difFeedCard = mutableStateOf<List<MyFeedCardDataModel.MyFeedCardDto>>(emptyList())
        private set

    val retrofitInstance = SooumApplication().instance.create(CardApi::class.java)

    var isBlock = mutableStateOf<Boolean>(false)


    fun getDifProfile(memberId: Long) {
        viewModelScope.launch {
            difProfile.value = difProfileUseCase(memberId)
        }
    }

    fun getDifFeedCard(memberId: Long) {
        viewModelScope.launch {
            difFeedCard.value = difFeedCardUseCase(memberId)
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

    fun userBlock(memberId: Long) {
        viewModelScope.launch {
            try {
                retrofitInstance.userBlocks(BlockBody(memberId))
                isBlock.value = true
            } catch (E: Exception) {
                println(E)
            }

        }
    }

    fun deleteUserBlock(userId: Long) {
        viewModelScope.launch {
            try {
                retrofitInstance.deleteUserBlocks(userId)
                isBlock.value = false
            } catch (E: Exception) {
                println(E)
            }
        }
    }

}

