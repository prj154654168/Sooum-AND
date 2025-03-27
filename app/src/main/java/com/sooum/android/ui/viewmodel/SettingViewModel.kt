package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.NotifyBody
import com.sooum.android.domain.usecase.profile.GetNotifyUseCase
import com.sooum.android.domain.usecase.profile.UpdateNotifyUseCase
import com.sooum.android.domain.usecase.profile.UserActiveStatusUseCase
import com.sooum.android.domain.usecase.profile.UserCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getNotifyUseCase: GetNotifyUseCase,
    private val updateNotifyUseCase: UpdateNotifyUseCase,
    private val userActiveStatusUseCase: UserActiveStatusUseCase,
) : ViewModel() {
    var isNotify = mutableStateOf<Boolean>(false)
        private set

    var banDate = mutableStateOf("")
    private set

    init {
        getNotify()
        getUserActiveStatus()
    }

    fun getNotify() {
        viewModelScope.launch {
            try {
                val response = getNotifyUseCase()
                isNotify.value = response.isAllowNotify
            } catch (E: Exception) {
                println(E)
            }
        }
    }

    fun updateNotify() {
        viewModelScope.launch {
            try {
                val response = updateNotifyUseCase(NotifyBody(!isNotify.value))
                isNotify.value = !isNotify.value
            } catch (E: Exception) {
                println(E)
            }
        }
    }

    fun getUserActiveStatus() {
        viewModelScope.launch {
            try {
                val response = userActiveStatusUseCase()

                // 날짜 포맷 설정: "YYYY년 MM월 dd일" 형식으로 변환
                val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                banDate.value = if (response.banEndAt != null) {
                    dateFormat.format(response.banEndAt)  // Date를 "YYYY년 MM월 dd일" 형식의 String으로 변환
                } else {
                    ""  // null인 경우 대체 문자열
                }

                // Log.e("asd", "테스트: banEndAt=$banDate, $response")
            } catch (E: Exception) {
                println(E)
            }
        }
    }

}