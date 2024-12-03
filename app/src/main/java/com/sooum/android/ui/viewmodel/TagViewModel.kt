package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.usecase.tag.RecommendTagUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(private val recommendTagUseCase: RecommendTagUseCase) : ViewModel() {
    var recommendTagList = mutableStateListOf<RecommendTagDataModel.Embedded.RecommendTag>()
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
}