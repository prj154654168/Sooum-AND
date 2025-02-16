package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sooum.android.User
import com.sooum.android.domain.model.FavoriteTagDataModel
import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.SearchTagDataModel
import com.sooum.android.domain.model.SortedByLatestDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.model.TagFeedDataModel
import com.sooum.android.domain.model.TagSummaryDataModel
import com.sooum.android.domain.usecase.tag.DeleteTagFavoriteUseCase
import com.sooum.android.domain.usecase.tag.FavoriteTagUseCase
import com.sooum.android.domain.usecase.tag.PostTagFavoriteUseCase
import com.sooum.android.domain.usecase.tag.RecommendTagUseCase
import com.sooum.android.domain.usecase.tag.SearchTagUseCase
import com.sooum.android.domain.usecase.tag.TagFeedUseCase
import com.sooum.android.domain.usecase.tag.TagSummaryUseCase
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
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val recommendTagUseCase: RecommendTagUseCase,
    private val tagSummaryUseCase: TagSummaryUseCase,
    private val postTagFavoriteUseCase: PostTagFavoriteUseCase,
    private val deleteTagFavoriteUseCase: DeleteTagFavoriteUseCase,
    private val getFavoriteTagUseCase: FavoriteTagUseCase,
    private val tagFeedUseCase: TagFeedUseCase,
    private val searchTagUseCase: SearchTagUseCase
) : ViewModel() {
    var recommendTagList = mutableStateListOf<RecommendTagDataModel.Embedded.RecommendTag>()
        private set

    var tagSummary by mutableStateOf<TagSummaryDataModel?>(null)
        private set

    var favoriteTagList = mutableStateListOf<FavoriteTagDataModel.Embedded.FavoriteTag>()
        private set

//    var tagFeedList = mutableStateListOf<TagFeedDataModel.Embedded.TagFeedCardDto>()

//    var searchTagList = mutableStateListOf<SearchTagDataModel.Embedded.RelatedTag>()

    private val _lazyTagFeed = MutableStateFlow<Flow<PagingData<TagFeedDataModel.Embedded.TagFeedCardDto>>?>(null)
    val lazyTagFeed = _lazyTagFeed.asStateFlow()

//    val lazyTagFeed = tagFeedUseCase(tagId, User.userInfo.latitude, User.userInfo.longitude).cachedIn(viewModelScope)

    // 사용자 입력값을 저장하는 Flow
    private val _query = MutableStateFlow("")

    // 연관 검색어 결과를 저장하는 StateFlow
    private val _suggestions = MutableStateFlow<List<SearchTagDataModel.Embedded.RelatedTag>>(emptyList())
    val suggestions: StateFlow<List<SearchTagDataModel.Embedded.RelatedTag>> = _suggestions.asStateFlow()

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

    fun getTagSummary(tagId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val summary = tagSummaryUseCase(tagId)
                onResult(summary.isFavorite)
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

    fun loadTagFeed(tagId: String) {
        _lazyTagFeed.value = tagFeedUseCase(tagId, User.userInfo.latitude, User.userInfo.longitude).cachedIn(viewModelScope)
    }

//    fun getTagFeedList(tagId: String, latitude: Double?, longitude: Double?, laskPk: Long?) {
//        viewModelScope.launch {
//            try {
//                val response = tagFeedUseCase(tagId, latitude, longitude, laskPk)
//
//                tagFeedList.clear()
//                tagFeedList.addAll(response._embedded.tagFeedCardDtoList)
//            }
//            catch (e: Exception) {
//                Log.e("HomeViewModel", e.toString())
//            }
//        }
//    }

    // API 호출 함수
    private fun fetchSuggestions(query: String): Flow<List<SearchTagDataModel.Embedded.RelatedTag>> = flow {
        try {
            val result = searchTagUseCase(query).embedded.relatedTagList // API 요청
            emit(result)
        } catch (e: Exception) {
            emit(emptyList()) // 실패 시 빈 리스트 반환
        }
    }

//    fun getSearchTag(keyword: String) {
//        viewModelScope.launch {
//            try {
//                val response = searchTagUseCase(keyword)
//
//                searchTagList.clear()
//                searchTagList.addAll(response.embedded.relatedTagList)
//            }
//            catch (e: Exception) {
//                Log.e("HomeViewModel", e.toString())
//            }
//        }
//    }
}