package com.sooum.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.SooumApplication
import com.sooum.android.domain.model.DeleteUserBody
import com.sooum.android.domain.usecase.profile.DeleteUserUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDeleteViewModel @Inject constructor(
    private val deleteUserUserCase: DeleteUserUserCase,
) :
    ViewModel() {

    fun deleteUser() {
        viewModelScope.launch {
            try {
                deleteUserUserCase(
                    DeleteUserBody(
                        SooumApplication().getVariable("accessToken"),
                        SooumApplication().getVariable("refreshToken")
                    )
                )
            }catch (E:Exception){
                println(E)
            }
        }
    }
}