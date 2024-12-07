package com.sooum.android.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.usecase.profile.DeleteUserUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDeleteViewModel @Inject constructor(
    private val deleteUserUserCase: DeleteUserUserCase,
) :
    ViewModel() {

    fun deleteUser(activity: Activity?) {
        viewModelScope.launch {
            deleteUserUserCase()
            activity?.finish()
        }
    }
}