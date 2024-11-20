package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sooum.android.data.remote.CardApi
import com.sooum.android.Constants.ACCESS_TOKEN
import com.sooum.android.data.remote.RetrofitInterface
import com.sooum.android.domain.model.DefaultImageDataModel
import com.sooum.android.domain.model.ImageIssueDataModel
import com.sooum.android.domain.model.RelatedTagDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.usecase.postcard.DefaultImageUseCase
import com.sooum.android.domain.usecase.postcard.FeedCardUseCase
import com.sooum.android.domain.usecase.postcard.RelatedTagUseCase
import com.sooum.android.enums.FontEnum
import com.sooum.android.enums.ImgTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val getDefaultImageUseCase: DefaultImageUseCase,
    private val getRelatedTagUseCase: RelatedTagUseCase,
    private val postFeedCardUseCase: FeedCardUseCase
) : ViewModel() {
    var defaultImageList = mutableStateListOf<DefaultImageDataModel.Embedded.ImgUrlInfo>()
        private set

    var refreshImageQuery = ""

    var selectedImageForDefault: String by mutableStateOf(null.toString())

    var relatedTagList = mutableStateListOf<RelatedTagDataModel.Embedded.RelatedTag>()
        private set

    val cardAPIInstance = RetrofitInterface.getInstance().create(CardApi::class.java)

    var postFeedCardStatus by mutableStateOf<Status?>(null)
        private set

    var userImageUrl by mutableStateOf<String?>(null)

    fun postFeedCard(
        isDistanceShared: Boolean,
        latitude: Double?,
        longitude: Double?,
        isPublic: Boolean,
        isStory: Boolean,
        content: String,
        font: FontEnum,
        imgType: ImgTypeEnum,
        imgName: String,
        feedTags: List<String>
    ) {
        viewModelScope.launch {
            try {
                val responseBody = postFeedCardUseCase(
                    isDistanceShared,
                    latitude,
                    longitude,
                    isPublic,
                    isStory,
                    content,
                    font,
                    imgType,
                    imgName,
                    feedTags
                )
                postFeedCardStatus = responseBody
                Log.d("AddPostViewModel", postFeedCardStatus?.httpCode.toString())
            }
            catch (e: Exception) {
                Log.e("AddPostViewModel", e.toString())
            }
        }
    }

    fun getDefaultImageList() {
        viewModelScope.launch {
            try {
                val responseBody = getDefaultImageUseCase(null)
                val imageList = responseBody.embedded.imgUrlInfoList
                defaultImageList.clear()
                defaultImageList.addAll(imageList)
                refreshImageQuery = getPreviousImages(responseBody.links.next.href)
                selectedImageForDefault = defaultImageList[0].url.href
            } catch (e: Exception) {
                Log.e("AddPostViewModel", e.printStackTrace().toString())
            }
        }
    }

    fun refreshDefaultImageList() {
        viewModelScope.launch {
            try {
                val responseBody = getDefaultImageUseCase(refreshImageQuery)
                val imageList = responseBody.embedded.imgUrlInfoList
                defaultImageList.clear()
                defaultImageList.addAll(imageList)
                refreshImageQuery = getPreviousImages(responseBody.links.next.href)
                selectedImageForDefault = defaultImageList[0].url.href
            } catch (e: Exception) {
                Log.e("AddPostViewModel", e.printStackTrace().toString())
            }
        }
    }

    fun getImageUrl(byteArray: ByteArray) {
        viewModelScope.launch {
            val urlResponse = cardAPIInstance.getImageUrl(ACCESS_TOKEN).body()
            Log.e("response",urlResponse.toString())

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
        viewModelScope.launch {
            try {
                val tagList = getRelatedTagUseCase(keyword, size)
                relatedTagList.addAll(tagList)
            } catch (e: Exception) {
                Log.e("AddPostViewModel", e.printStackTrace().toString())
            }
        }
    }

    private fun getPreviousImages(url: String): String {
        return url.split("?")[1].split("=")[1]
    }
}