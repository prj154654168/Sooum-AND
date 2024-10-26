package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.data.remote.CardApi
import com.sooum.android.Constants
import com.sooum.android.data.remote.RetrofitInterface
import com.sooum.android.domain.model.DetailCardLikeCommentCountDataModel
import com.sooum.android.domain.model.DetailCommentCardDataModel
import com.sooum.android.domain.model.FeedCardDataModel
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    var feedCardDataModel: FeedCardDataModel? by mutableStateOf(null)
    var detailCardLikeCommentCountDataModel: DetailCardLikeCommentCountDataModel? by mutableStateOf(
        null
    )
    var detailCommentCardDataModel: DetailCommentCardDataModel? by mutableStateOf(
        null
    )
        private set
    val retrofitInstance = RetrofitInterface.getInstance().create(CardApi::class.java)

    fun getFeedCard(cardId: Long) {
        viewModelScope.launch {
            feedCardDataModel = retrofitInstance.getFeedCard(Constants.ACCESS_TOKEN, cardId).body()
        }
    }

    fun getDetailCardLikeCommentCount(cardId: Long) {
        viewModelScope.launch {
            detailCardLikeCommentCountDataModel =
                retrofitInstance.getCardLikeCommentCount(Constants.ACCESS_TOKEN, cardId).body()

        }
    }

    fun getDetailCommentCard(cardId: Long) {
        viewModelScope.launch {
            detailCommentCardDataModel =
                retrofitInstance.getDeatilCommentCard(Constants.ACCESS_TOKEN, cardId).body()
        }
    }

    fun likeOn(cardId: Long) {
        viewModelScope.launch {
            retrofitInstance.likeOn(Constants.ACCESS_TOKEN, cardId).body()
            getDetailCardLikeCommentCount(cardId)
        }
    }

    fun likeOff(cardId: Long) {
        viewModelScope.launch {
            retrofitInstance.likeOff(Constants.ACCESS_TOKEN, cardId).body()
            getDetailCardLikeCommentCount(cardId)
        }
    }

    fun userBlocks() {
        viewModelScope.launch {
            val temp = feedCardDataModel?.member?.id?.let {
                retrofitInstance.userBlocks(
                    Constants.ACCESS_TOKEN,
                    it.toLong()
                ).body()
            }
            Log.e("temp", feedCardDataModel?.member?.id.toString())
        }
    }

    fun deleteCard(cardId: Long) {
        viewModelScope.launch {
            retrofitInstance.deleteCard(Constants.ACCESS_TOKEN, cardId)
        }
    }

}