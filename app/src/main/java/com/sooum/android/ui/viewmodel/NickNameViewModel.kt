package com.sooum.android.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.NicknameBody
import com.sooum.android.domain.usecase.profile.NicknameAvailableUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NickNameViewModel @Inject constructor(
    private val nicknameAvailableUseCase: NicknameAvailableUseCase
) : ViewModel() {
    var isNicknameAvailable by mutableStateOf(true)
    fun nicknameAvailable(nickname: String, function: () -> Unit) {
        viewModelScope.launch {
            val response = nicknameAvailableUseCase(NicknameBody(nickname))
            if (response.isAvailable) {
                function()
            } else {
                isNicknameAvailable = false
            }
        }
    }
}
