package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.model.MyProfileDataModel
import com.sooum.android.domain.usecase.profile.MyFeedCardUseCase
import com.sooum.android.domain.usecase.profile.MyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val myProfileUseCase: MyProfileUseCase,
    private val myFeedCardUseCase: MyFeedCardUseCase,
) :
    ViewModel() {
    var myProfile = mutableStateOf<MyProfileDataModel?>(null)
        private set
    var myFeedCard = mutableStateOf<List<MyFeedCardDataModel.MyFeedCardDto>>(emptyList())
        private set


    init {
        getMyProfile()
        getMyFeedCard()
    }

    fun getMyProfile() {
        viewModelScope.launch {
            try {
                myProfile.value = myProfileUseCase()
            }catch (E:Exception){
                println(E)
            }

        }
    }

    fun getMyFeedCard() {
        viewModelScope.launch {
            try {
                myFeedCard.value = myFeedCardUseCase()
            }catch (E:Exception){
                println(E)
            }
        }
    }
}
