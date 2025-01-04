package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.NotifyBody
import com.sooum.android.domain.usecase.profile.GetNotifyUseCase
import com.sooum.android.domain.usecase.profile.UpdateNotifyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getNotifyUseCase: GetNotifyUseCase,
    private val updateNotifyUseCase: UpdateNotifyUseCase,
) : ViewModel() {
    var isNotify = mutableStateOf<Boolean>(true)
        private set

    init{
        getNotify()
    }

    fun getNotify(){
        viewModelScope.launch {
            try {
                val response = getNotifyUseCase

            }catch (E:Exception){
                println(E)
            }
        }
    }

    fun updateNotify(){
        viewModelScope.launch {
            try {
                val response = updateNotifyUseCase(NotifyBody(isNotify.value))
            }catch (E:Exception){
                println(E)
            }
        }
    }



}