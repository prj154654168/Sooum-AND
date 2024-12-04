package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.TagSummaryDataModel
import com.sooum.android.domain.usecase.tag.RecommendTagUseCase
import com.sooum.android.domain.usecase.tag.TagSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val recommendTagUseCase: RecommendTagUseCase,
    private val tagSummaryUseCase: TagSummaryUseCase
) : ViewModel() {
    var recommendTagList = mutableStateListOf<RecommendTagDataModel.Embedded.RecommendTag>()
        private set

    var tagSummary by mutableStateOf<TagSummaryDataModel?>(null)
        private set

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
}