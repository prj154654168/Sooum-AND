package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.sooum.android.SooumApplication
import com.sooum.android.data.remote.CardApi

import com.sooum.android.domain.model.Token
import com.sooum.android.domain.model.signUpModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _fcmToken = MutableStateFlow<String>("")
    val fcmToken: StateFlow<String> = _fcmToken

    /**
     * FCM 토큰 발급
     */
     fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    _fcmToken.value = token
                    Log.d("MainViewModel","fcmToken: $token")
                } else {
                    Log.e("MainViewModel","FCM 토큰 발급 실패 : ${task.exception}")
                }
            }
    }
}