package com.sooum.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.SooumApplication
import com.sooum.android.domain.model.UserCodeBody
import com.sooum.android.domain.usecase.profile.PostUserCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterUserCodeViewModel @Inject constructor(
    private val postUserCodeUseCase: PostUserCodeUseCase,
) :
    ViewModel() {

    fun postUserCode(code: String) {
        viewModelScope.launch {
            try {
                postUserCodeUseCase(
                    UserCodeBody(
                        code, SooumApplication().getVariable(
                            "encryptedDeviceId",
                        )
                    )
                )
            } catch (E: Exception) {
                println(E)
            }
        }
    }
}