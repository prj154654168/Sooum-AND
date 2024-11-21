package com.sooum.android.ui.viewmodel

import android.content.Context
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.RetrofitInterface

class LogInViewModel : ViewModel() {
    val retrofitInstance = RetrofitInterface.getInstance().create(CardApi::class.java)

}