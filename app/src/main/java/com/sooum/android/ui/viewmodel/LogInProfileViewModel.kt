package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.SooumApplication
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.NicknameBody
import com.sooum.android.domain.model.profileBody
import com.sooum.android.domain.usecase.profile.MyProfileUseCase
import com.sooum.android.domain.usecase.profile.NicknameAvailableUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LogInProfileViewModel @Inject constructor(
    private val myProfileUseCase: MyProfileUseCase,
    private val nicknameAvailableUseCase: NicknameAvailableUseCase
) :
    ViewModel() {
    val cardAPIInstance = SooumApplication().instance.create(CardApi::class.java)
    var userImageUrl by mutableStateOf<String?>(null)
    var imgByteArray by mutableStateOf<ByteArray>(ByteArray(0))
    var isLoading by mutableIntStateOf(0)
    var isNicknameAvailable by mutableStateOf(true)
    var isSuccess by mutableStateOf(false)

    var myProfileNickName = mutableStateOf<String>("")
    var myProfileImgUrl = mutableStateOf<String>("")

    fun getMyProfile() {
        viewModelScope.launch {
            try {
                val response = myProfileUseCase()
                myProfileImgUrl.value =
                    if (response.profileImg != null) response.profileImg.href else ""
                myProfileNickName.value = response.nickname
            } catch (E: Exception) {
                println(E)
            }
        }
    }

    fun profiles(nickname: String, mode: Int) {
        viewModelScope.launch {
            try {
                val response = nicknameAvailableUseCase(NicknameBody(nickname))
                if (response.isAvailable) {
                    if (mode == 1) {
                        val urlResponse = cardAPIInstance.getProfileImageUrl().body()
                        Log.e("response", urlResponse.toString())

                        if (urlResponse != null) {
                            userImageUrl = urlResponse.imgName
                            val client = OkHttpClient()

                            val mediaType = "image/jpeg".toMediaTypeOrNull()
                            val requestBody = RequestBody.create(mediaType, imgByteArray)

                            val request = Request.Builder()
                                .url(urlResponse.url.href)
                                .put(requestBody)
                                .addHeader("Content-Type", "image/jpeg")
                                .build()

                            makeRequest(client, request)
                        }
                        if (cardAPIInstance.profiles(
                                profileBody(
                                    nickname,
                                    userImageUrl.toString()
                                )
                            ).isSuccessful
                        ) {
                            isSuccess = true
                        }

                    } else {
                        if (cardAPIInstance.profiles(profileBody(nickname, null)).isSuccessful) {
                            isSuccess = true
                        }
                    }
                } else {
                    isNicknameAvailable = false
                }
                isLoading = 1
            } catch (E: Exception) {
                println(E)
            }
        }
    }

    suspend fun makeRequest(client: OkHttpClient, request: Request): Response {
        return withContext(Dispatchers.IO) {
            try {
                client.newCall(request).execute() // 동기 호출로 변경
            } catch (e: IOException) {
                throw e // 네트워크 오류 처리
            }
        }
    }
}