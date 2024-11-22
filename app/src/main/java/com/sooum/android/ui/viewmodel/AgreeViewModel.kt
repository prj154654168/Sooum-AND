package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.RetrofitInterface
import com.sooum.android.domain.model.signUpModel
import kotlinx.coroutines.launch

class AgreeViewModel : ViewModel() {
    val retrofitInstance = RetrofitInterface.getInstance().create(CardApi::class.java)

    fun signUp(signUpModel: signUpModel) {
        viewModelScope.launch {
            try {
                val b= retrofitInstance.signUp(signUpModel)
                Log.e("convert",b.toString())
            } catch (E: Exception) {
                println(E)
            }
        }
    }
}