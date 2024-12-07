package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.FavoriteTagDataModel
import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.model.TagFeedDataModel
import com.sooum.android.domain.model.TagSummaryDataModel
import com.sooum.android.domain.usecase.tag.DeleteTagFavoriteUseCase
import com.sooum.android.domain.usecase.tag.FavoriteTagUseCase
import com.sooum.android.domain.usecase.tag.PostTagFavoriteUseCase
import com.sooum.android.domain.usecase.tag.RecommendTagUseCase
import com.sooum.android.domain.usecase.tag.TagFeedUseCase
import com.sooum.android.domain.usecase.tag.TagSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val recommendTagUseCase: RecommendTagUseCase,
    private val tagSummaryUseCase: TagSummaryUseCase,
    private val postTagFavoriteUseCase: PostTagFavoriteUseCase,
    private val deleteTagFavoriteUseCase: DeleteTagFavoriteUseCase,
    private val getFavoriteTagUseCase: FavoriteTagUseCase,
    private val tagFeedUseCase: TagFeedUseCase
) : ViewModel() {
    var recommendTagList = mutableStateListOf<RecommendTagDataModel.Embedded.RecommendTag>()
        private set

    var tagSummary by mutableStateOf<TagSummaryDataModel?>(null)
        private set

    var favoriteTagList = mutableStateListOf<FavoriteTagDataModel.Embedded.FavoriteTag>()
        private set

    var tagFeedList = mutableStateListOf<TagFeedDataModel.Embedded.TagFeedCardDto>()

    fun getRecommendTagList() {
        viewModelScope.launch {
            try {
                val list = recommendTagUseCase()
                recommendTagList.clear()
                recommendTagList.addAll(list)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
        }
    }

    fun getTagSummary(tagId: String) {
        viewModelScope.launch {
            try {
                val summary = tagSummaryUseCase(tagId)
                tagSummary = summary
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
        }
    }

    fun postTagFavorite(tagId: String, onItemClick: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val status = postTagFavoriteUseCase(tagId)
                if (status.httpCode == 201) {
                    onItemClick(201)
                }
                else {
                    onItemClick(400)
                }
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
                onItemClick(400)
            }
        }
    }

    fun deleteTagFavorite(tagId: String, onItemClick: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val status = deleteTagFavoriteUseCase(tagId)
                if (status.httpCode == 204) {
                    onItemClick(204)
                }
                else {
                    onItemClick(400)
                }
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
                onItemClick(400)
            }
        }
    }

    fun getFavoriteTag(last: String?) {
        viewModelScope.launch {
            try {
                val response = getFavoriteTagUseCase(last)

                favoriteTagList.clear()
                favoriteTagList.addAll(response.embedded.favoriteTagList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun getTagFeedList(tagId: String, latitude: Double?, longitude: Double?, laskPk: Long?) {
        viewModelScope.launch {
            try {
                val response = tagFeedUseCase(tagId, latitude, longitude, laskPk)

                tagFeedList.clear()
                tagFeedList.addAll(response._embedded.tagFeedCardDtoList)
            }
            catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }
}