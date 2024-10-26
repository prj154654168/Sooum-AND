package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.data.remote.CardApi
import com.sooum.android.Constants.ACCESS_TOKEN
import com.sooum.android.data.remote.RetrofitInterface
import com.sooum.android.data.remote.TagAPI
import com.sooum.android.domain.model.DefaultImageDataModel
import com.sooum.android.domain.model.RelatedTagDataModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class AddPostViewModel : ViewModel() {
    var defaultImageList = mutableStateListOf<DefaultImageDataModel.Embedded.ImgUrlInfo>()
        private set

    var refreshImageQuery = ""

    var nowImage: String by mutableStateOf(null.toString())

    var relatedTagList = mutableStateListOf<RelatedTagDataModel.Embedded.RelatedTag>()
        private set

    val cardAPIInstance = RetrofitInterface.getInstance().create(CardApi::class.java)
    val tagAPIInstance = RetrofitInterface.getInstance().create(TagAPI::class.java)

    fun getDefaultImageList() {
        try {
            viewModelScope.launch {
                val defaultImageResponse = cardAPIInstance.getDefaultImage(ACCESS_TOKEN)
                if (defaultImageResponse.isSuccessful) {
                    val defaultImageBody = defaultImageResponse.body()
                    if (defaultImageBody != null) {
                        defaultImageList.clear()
                        defaultImageList.addAll(defaultImageBody.embedded.imgUrlInfoList)
                        refreshImageQuery = getPreviousImages(defaultImageBody.links.next.href)
                        nowImage = defaultImageList[0].url.href
                        Log.d(
                            "AddPostViewModel",
                            "defaultImage load success ${defaultImageList[1]}"
                        )
                    } else {
                        Log.d("AddPostViewModel", "defaultImage body is null")
                    }
                } else {
                    Log.d("AddPostViewModel", "getDefaultImageList fail")
                }
            }
        } catch (e: Exception) {
            Log.e("AddPostViewModel", e.printStackTrace().toString())
        }
    }

    fun refreshDefaultImageList() {
        try {
            viewModelScope.launch {
                val defaultImageResponse =
                    cardAPIInstance.getDefaultImage(ACCESS_TOKEN, refreshImageQuery)
                if (defaultImageResponse.isSuccessful) {
                    val defaultImageBody = defaultImageResponse.body()
                    if (defaultImageBody != null) {
                        defaultImageList.clear()
                        defaultImageList.addAll(defaultImageBody.embedded.imgUrlInfoList)
                        refreshImageQuery = getPreviousImages(defaultImageBody.links.next.href)
                        nowImage = defaultImageList[0].url.href
                        Log.d("nowImage", nowImage)
                        Log.d(
                            "AddPostViewModel",
                            "defaultImage load success ${defaultImageList[1]}"
                        )
                    } else {
                        Log.d("AddPostViewModel", "defaultImage body is null")
                    }
                } else {
                    Log.d("AddPostViewModel", "getDefaultImageList fail")
                }
            }
        } catch (e: Exception) {
            Log.e("AddPostViewModel", e.printStackTrace().toString())
        }
    }

    private fun getPreviousImages(url: String): String {
        return url.split("?")[1].split("=")[1]
    }

    fun getImageUrl(byteArray: ByteArray) {
        viewModelScope.launch {
            val urlResponse = cardAPIInstance.getImageUrl(ACCESS_TOKEN).body()
            //Log.e("urlResponse",urlResponse.toString())
            if (urlResponse != null) {
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
                            Log.e("response",response.toString())
                        } else {
                            println("Upload failed: ${response.message}")
                        }
                    }
                })
            }
        }

    }

    fun getRelatedTag(keyword: String, size: Int) {
        try {
            viewModelScope.launch {
                val relatedTagResponse = tagAPIInstance.getRelatedTag(ACCESS_TOKEN, keyword, size)
                if (relatedTagResponse.isSuccessful) {
                    val relatedTagBody = relatedTagResponse.body()
                    if (relatedTagBody != null) {
//                        relatedTagList.clear()
//                        relatedTagList.addAll(relatedTagBody.embedded.relatedTagList)
                    } else {
                        Log.d("AddPostViewModel", "defaultImage body is null")
                    }
                } else {
                    Log.d("AddPostViewModel", "getDefaultImageList fail")
                }
            }
        } catch (e: Exception) {
            Log.e("AddPostViewModel", e.printStackTrace().toString())
        }
    }
}