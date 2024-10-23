package com.sooum.android.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.CardApi
import com.sooum.android.Constants.ACCESS_TOKEN
import com.sooum.android.RetrofitInterface
import com.sooum.android.TagAPI
import com.sooum.android.model.DefaultImageDataModel
import com.sooum.android.model.RelatedTagDataModel
import com.sooum.android.model.SortedByLatestDataModel
import com.sooum.android.model.SortedByPopularityDataModel
import kotlinx.coroutines.launch

class AddPostViewModel : ViewModel() {
    var defaultImageList = mutableStateListOf<DefaultImageDataModel.Embedded.ImgUrlInfo>()
        private set

    var refreshImageQuery = ""

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
                        Log.d("AddPostViewModel", "defaultImage load success ${defaultImageList[1]}")
                    } else {
                        Log.d("AddPostViewModel", "defaultImage body is null")
                    }
                } else {
                    Log.d("AddPostViewModel", "getDefaultImageList fail")
                }
            }
        }
        catch (e: Exception) {
            Log.e("AddPostViewModel", e.printStackTrace().toString())
        }
    }

    fun refreshDefaultImageList() {
        try {
            viewModelScope.launch {
                val defaultImageResponse = cardAPIInstance.getDefaultImage(ACCESS_TOKEN, refreshImageQuery)
                if (defaultImageResponse.isSuccessful) {
                    val defaultImageBody = defaultImageResponse.body()
                    if (defaultImageBody != null) {
                        defaultImageList.clear()
                        defaultImageList.addAll(defaultImageBody.embedded.imgUrlInfoList)
                        refreshImageQuery = getPreviousImages(defaultImageBody.links.next.href)
                        Log.d("AddPostViewModel", "defaultImage load success ${defaultImageList[1]}")
                    } else {
                        Log.d("AddPostViewModel", "defaultImage body is null")
                    }
                } else {
                    Log.d("AddPostViewModel", "getDefaultImageList fail")
                }
            }
        }
        catch (e: Exception) {
            Log.e("AddPostViewModel", e.printStackTrace().toString())
        }
    }

    private fun getPreviousImages(url: String): String {
        return url.split("?")[1].split("=")[1]
    }

    fun getRelatedTag(keyword : String, size : Int) {
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
        }
        catch (e: Exception) {
            Log.e("AddPostViewModel", e.printStackTrace().toString())
        }
    }
}