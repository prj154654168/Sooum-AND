package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.usecase.profile.PatchUserCodeUseCase
import com.sooum.android.domain.usecase.profile.UserCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MakeUserCodeViewModel @Inject constructor(
    private val userCodeUseCase: UserCodeUseCase,
    private val patchUserCodeUseCase: PatchUserCodeUseCase,
) :
    ViewModel() {

    var code = mutableStateOf<String>("")
        private set

    init {
        getCode()
    }

    fun getCode() {
        viewModelScope.launch {
            code.value = userCodeUseCase().transferCode

        }
    }

    fun patchCode() {
        viewModelScope.launch {
            code.value = patchUserCodeUseCase().transferCode

        }
    }
}