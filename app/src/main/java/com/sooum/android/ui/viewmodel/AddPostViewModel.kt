package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.SooumApplication
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.DefaultImageDataModel
import com.sooum.android.domain.model.PostCommentCardRequestDataModel
import com.sooum.android.domain.model.RelatedTagDataModel
import com.sooum.android.domain.model.SearchTagDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.usecase.detail.PostCommentCardUseCase
import com.sooum.android.domain.usecase.postcard.DefaultImageUseCase
import com.sooum.android.domain.usecase.postcard.FeedCardUseCase
import com.sooum.android.domain.usecase.postcard.RelatedTagUseCase
import com.sooum.android.enums.FontEnum
import com.sooum.android.enums.ImgTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val postFeedCardUseCase: FeedCardUseCase,
    private val postCommentCardUseCase: PostCommentCardUseCase
) : ViewModel() {
    var defaultImageList = mutableStateListOf<DefaultImageDataModel.Embedded.ImgUrlInfo>()
        private set

    var refreshImageQuery = ""

    var selectedImageForDefault: String by mutableStateOf(null.toString())

    var selectedImageName : String by mutableStateOf(null.toString())

    var relatedTagList = mutableStateListOf<RelatedTagDataModel.Embedded.RelatedTag>()

    val cardAPIInstance = SooumApplication().instance.create(CardApi::class.java)

    var postFeedCardStatus by mutableStateOf<Status?>(null)
        private set

    var postCommentCardStatus by mutableStateOf<Status?>(null)
        private set

    var userImageUrl by mutableStateOf<String?>(null)

    // 사용자 입력값을 저장하는 Flow
    private val _query = MutableStateFlow("")

    // 연관 검색어 결과를 저장하는 StateFlow
    private val _suggestions = MutableStateFlow<List<RelatedTagDataModel.Embedded.RelatedTag>>(emptyList())
    val suggestions: StateFlow<List<RelatedTagDataModel.Embedded.RelatedTag>> = _suggestions.asStateFlow()

    init {
        // 입력값이 변경될 때마다 API 요청 (디바운스 적용)
        _query
            .debounce(300) // 사용자가 입력을 멈춘 후 300ms 뒤에 요청
            .distinctUntilChanged() // 같은 값이면 요청 안 함
            .filter { it.isNotBlank() } // 빈 문자열이면 요청 안 함
            .flatMapLatest { query ->
                fetchSuggestions(query)
            }
            .flowOn(Dispatchers.IO)
            .onEach { result -> _suggestions.value = result }
            .launchIn(viewModelScope)
    }

    // 사용자가 입력한 검색어 업데이트
    fun onQueryChanged(query: String) {
        _query.value = query
    }

    // 🔹 검색어를 초기화하는 함수
    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }

    // API 호출 함수
    private fun fetchSuggestions(query: String): Flow<List<RelatedTagDataModel.Embedded.RelatedTag>> = flow {
        try {
            val result = getRelatedTagUseCase(query, 5) // API 요청
            emit(result)
        } catch (e: Exception) {
            emit(emptyList()) // 실패 시 빈 리스트 반환
        }
    }

    fun postCommentCard(
        cardId: Long,
        commentCardRequest: PostCommentCardRequestDataModel,
        onStatusChanged: (Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val responseBody = postCommentCardUseCase(cardId, commentCardRequest)
                postCommentCardStatus = responseBody

                if (postCommentCardStatus?.httpCode == 201) {
                    onStatusChanged(201)
                }
            } catch (e: Exception) {
                Log.e("AddPostViewModel", e.toString())
            }
        }
    }

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
        feedTags: List<String>?,
        onStatusChanged: (Int) -> Unit
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
                if (postFeedCardStatus?.httpCode == 201) {
                    onStatusChanged(201)
                }
                Log.d("AddPostViewModel", postFeedCardStatus?.httpCode.toString())
            } catch (e: Exception) {
                Log.e("AddPostViewModel", e.toString())
                // 예외 메시지에서 "400 Bad Request"를 포함하는 경우 처리
                if (e.message?.contains("400") == true) {
                    Log.d("AddPostViewModel","부적절한 사진 발생")
                    onStatusChanged(400)
                }
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
                selectedImageName = defaultImageList[0].imgName
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
                selectedImageName = defaultImageList[0].imgName
            } catch (e: Exception) {
                Log.e("AddPostViewModel", e.printStackTrace().toString())
            }
        }
    }

    fun getImageUrl(byteArray: ByteArray) {
        viewModelScope.launch {
            val urlResponse = cardAPIInstance.getImageUrl().body()
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

//    fun getRelatedTag(keyword: String, size: Int) {
//        viewModelScope.launch {
//            try {
//                val tagList = getRelatedTagUseCase(keyword, size)
//                relatedTagList.clear()
//                relatedTagList.addAll(tagList)
//                Log.d("123", "키워드 : ${keyword}, ${relatedTagList}")
//            } catch (e: Exception) {
//                Log.e("AddPostViewModel", e.printStackTrace().toString())
//            }
//        }
//    }

    private fun getPreviousImages(url: String): String {
        return url.split("?")[1].split("=")[1]
    }
}