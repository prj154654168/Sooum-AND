package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.Constants
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.RetrofitInterface
import com.sooum.android.domain.model.profileBody
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class LogInProfileViewModel : ViewModel() {
    val cardAPIInstance = RetrofitInterface.getInstance().create(CardApi::class.java)
    var userImageUrl by mutableStateOf<String?>(null)


    fun getImageUrl(byteArray: ByteArray) {
        viewModelScope.launch {
            val urlResponse = cardAPIInstance.getImageUrl(Constants.ACCESS_TOKEN).body()
            Log.e("response", urlResponse.toString())

            if (urlResponse != null) {
                userImageUrl = urlResponse.imgName
                val client = OkHttpClient()

                val mediaType = "image/jpeg".toMediaTypeOrNull()
                val requestBody = RequestBody.create(mediaType, byteArray)

                val request = Request.Builder()
                    .url(urlResponse.url.href)
                    .put(requestBody)
                    .addHeader("Content-Type", "image/jpeg")
                    .build()
                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: okhttp3.Call, response: Response) {
                        if (response.isSuccessful) {
                            Log.e("response", response.toString())
                        } else {
                            println("Upload failed: ${response.message}")
                        }
                    }
                })
            }
        }

    }

    fun profiles() {
        viewModelScope.launch {
            try {
                val b = cardAPIInstance.profiles("",profileBody("이현섭", userImageUrl.toString()))
                Log.e("convert",b.toString())
            } catch (E: Exception) {
                println(E)
            }
        }
    }
}