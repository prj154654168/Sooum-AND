package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.SooumApplication
import com.sooum.android.data.remote.CardApi

import com.sooum.android.domain.model.Token
import com.sooum.android.domain.model.signUpModel
import kotlinx.coroutines.launch

class AgreeViewModel : ViewModel() {
    val retrofitInstance = SooumApplication().instance.create(CardApi::class.java)
    fun signUp(signUpModel: signUpModel) {
        viewModelScope.launch {
            try {
                val b = retrofitInstance.signUp(signUpModel).body()

                if (b != null) {
                    SooumApplication().saveVariable("accessToken",b.token.accessToken)
                    SooumApplication().saveVariable("refreshToken",b.token.refreshToken)
                }
                Log.e("signUpModel", b.toString())
                Log.e("signUpModel", signUpModel.toString())
            } catch (E: Exception) {
                println(E)
            }
        }
    }
}