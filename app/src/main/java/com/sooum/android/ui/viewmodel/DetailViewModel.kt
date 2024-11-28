package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.SooumApplication
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.BlockBody

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
    val retrofitInstance = SooumApplication().instance.create(CardApi::class.java)

    fun getFeedCard(toLong: Double, longitude: Double, cardId: Long) {
        viewModelScope.launch {
            try {
                feedCardDataModel = retrofitInstance.getFeedCard(cardId, toLong, longitude).body()
                Log.e("feedCardDataModel",feedCardDataModel.toString())
            } catch (E: Exception) {
                println(E)
            }
        }
    }

    fun getDetailCardLikeCommentCount(cardId: Long) {
        viewModelScope.launch {
            try {
                detailCardLikeCommentCountDataModel =
                    retrofitInstance.getCardLikeCommentCount(cardId).body()
                Log.e("detailCardLikeCommentCountDataModel",detailCardLikeCommentCountDataModel.toString())
            } catch (E: Exception) {
                println(E)
            }
        }
    }

    fun getDetailCommentCard(cardId: Long, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                detailCommentCardDataModel =
                    retrofitInstance.getDeatilCommentCard(cardId, latitude, longitude).body()
                Log.e("detailCommentCardDataModel",detailCommentCardDataModel.toString())
            } catch (E: Exception) {
                println(E)
            }
        }
    }

    fun likeOn(cardId: Long) {
        viewModelScope.launch {
            retrofitInstance.likeOn(cardId).body()
            getDetailCardLikeCommentCount(cardId)
        }
    }

    fun likeOff(cardId: Long) {
        viewModelScope.launch {
            retrofitInstance.likeOff(cardId).body()
            getDetailCardLikeCommentCount(cardId)
        }
    }

    fun userBlocks() {
        viewModelScope.launch {
            val temp = feedCardDataModel?.member?.id?.let {
                retrofitInstance.userBlocks(
                    BlockBody(it.toLong())
                ).body()
            }
            Log.e("temp", feedCardDataModel?.member?.id.toString())
        }
    }

    fun deleteCard(cardId: Long) {
        viewModelScope.launch {
            try {
                retrofitInstance.deleteCard(cardId)
            } catch (E: Exception) {
                println(E)
            }

        }
    }

}