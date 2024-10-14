package com.sooum.android.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.CardApi
import com.sooum.android.Constants.ACCESS_TOKEN
import com.sooum.android.RetrofitInterface
import com.sooum.android.enums.DistanceEnum
import com.sooum.android.model.SortedByDistanceDataModel
import com.sooum.android.model.SortedByLatestDataModel
import com.sooum.android.model.SortedByPopularityDataModel
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    var latestCardList = mutableStateListOf<SortedByLatestDataModel.Embedded.LatestFeedCard>()
        private set

    var popularityCardList = mutableStateListOf<SortedByPopularityDataModel.Embedded.PopularFeedCard>()
        private set

    var distance1CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    var distance5CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    var distance10CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    var distance20CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    var distance50CardList = mutableStateListOf<SortedByDistanceDataModel.Embedded.DistanceFeedCard>()
        private set

    val retrofitInstance = RetrofitInterface.getInstance().create(CardApi::class.java)

    fun fetchLatestCardList(latitude: Double?, longitude: Double?) {
        try {
            viewModelScope.launch {
                val latestResponse = retrofitInstance.getLatestCardList(ACCESS_TOKEN, latitude, longitude)
                if (latestResponse.isSuccessful) {
                    Log.d("MainActivity", "latestReqSuccess")
                    val latestBody = latestResponse.body()
                    if (latestBody != null) {
                        latestCardList.addAll(latestBody.embedded.latestFeedCardDtoList)
                        Log.e("MainActivity", latestCardList.size.toString())
                    } else {
                        Log.d("MainActivity", "latest body is null")
                    }
                } else {
                    Log.d("MainActivity", "getLatestCardList fail")
                }
            }
        }
        catch (e: Exception) {
            Log.e("HomeViewModel", e.printStackTrace().toString())
        }
    }

    fun fetchPopularityCardList(latitude: Double?, longitude: Double?) {
        try {
            viewModelScope.launch {
                val popularityResponse = retrofitInstance.getPopularityCardList(ACCESS_TOKEN, latitude, longitude)
                if (popularityResponse.isSuccessful) {
                    Log.d("MainActivity", "popularityReqSuccess")
                    val popularityBody = popularityResponse.body()
                    if (popularityBody != null) {
                        popularityCardList.addAll(popularityBody.embedded.popularCardRetrieveList)
                    } else {
                        Log.d("MainActivity", "popularity body is null")
                    }
                } else {
                    Log.d("MainActivity", "getPopularityCardList fail")
                }
            }
        }
        catch (e: Exception) {
            Log.e("HomeViewModel", e.printStackTrace().toString())
        }
    }

    fun fetchDistance1CardList(latitude: Double, longitude: Double) {
        try {
            viewModelScope.launch {
                val distance1Response = retrofitInstance.getDistanceCardList(
                    ACCESS_TOKEN,
                    latitude,
                    longitude,
                    DistanceEnum.UNDER_1
                )
                if (distance1Response.isSuccessful) {
                    Log.d("MainActivity", "Distance1ReqSuccess")
                    val distanceBody = distance1Response.body()

                    if (distanceBody != null) {
                        distance1CardList.addAll(distanceBody.embedded.distanceCardDtoList)
                    } else {
                        Log.d("MainActivity", "distance body is null")
                    }
                } else {
                    Log.d("MainActivity", "getDistanceCardList fail")
                }
            }
        }
        catch (e: Exception) {
            Log.e("HomeViewModel", e.printStackTrace().toString())
        }
    }

    fun fetchDistance5CardList(latitude: Double, longitude: Double) {
        try {
            viewModelScope.launch {
                val distance5Response = retrofitInstance.getDistanceCardList(
                    ACCESS_TOKEN,
                    latitude,
                    longitude,
                    DistanceEnum.UNDER_5
                )
                if (distance5Response.isSuccessful) {
                    Log.d("MainActivity", "Distance1ReqSuccess")
                    val distanceBody = distance5Response.body()

                    if (distanceBody != null) {
                        distance5CardList.addAll(distanceBody.embedded.distanceCardDtoList)
                    } else {
                        Log.d("MainActivity", "distance body is null")
                    }
                } else {
                    Log.d("MainActivity", "getDistanceCardList fail")
                }
            }
        }
        catch (e: Exception) {
            Log.e("HomeViewModel", e.printStackTrace().toString())
        }
    }

    fun fetchDistance10CardList(latitude: Double, longitude: Double) {
        try {
            viewModelScope.launch {
                val distance10Response = retrofitInstance.getDistanceCardList(
                    ACCESS_TOKEN,
                    latitude,
                    longitude,
                    DistanceEnum.UNDER_10
                )
                if (distance10Response.isSuccessful) {
                    Log.d("MainActivity", "Distance1ReqSuccess")
                    val distanceBody = distance10Response.body()

                    if (distanceBody != null) {
                        distance10CardList.addAll(distanceBody.embedded.distanceCardDtoList)
                    } else {
                        Log.d("MainActivity", "distance body is null")
                    }
                } else {
                    Log.d("MainActivity", "getDistanceCardList fail")
                }
            }
        }
        catch (e: Exception) {
            Log.e("HomeViewModel", e.printStackTrace().toString())
        }
    }

    fun fetchDistance20CardList(latitude: Double, longitude: Double) {
        try {
            viewModelScope.launch {
                val distance20Response = retrofitInstance.getDistanceCardList(
                    ACCESS_TOKEN,
                    latitude,
                    longitude,
                    DistanceEnum.UNDER_20
                )
                if (distance20Response.isSuccessful) {
                    Log.d("MainActivity", "Distance1ReqSuccess")
                    val distanceBody = distance20Response.body()

                    if (distanceBody != null) {
                        distance20CardList.addAll(distanceBody.embedded.distanceCardDtoList)
                    } else {
                        Log.d("MainActivity", "distance body is null")
                    }
                } else {
                    Log.d("MainActivity", "getDistanceCardList fail")
                }
            }
        }
        catch (e: Exception) {
            Log.e("HomeViewModel", e.printStackTrace().toString())
        }
    }

    fun fetchDistance50CardList(latitude: Double, longitude: Double) {
        try {
            viewModelScope.launch {
                val distance50Response = retrofitInstance.getDistanceCardList(
                    ACCESS_TOKEN,
                    latitude,
                    longitude,
                    DistanceEnum.UNDER_5
                )
                if (distance50Response.isSuccessful) {
                    Log.d("MainActivity", "Distance1ReqSuccess")
                    val distanceBody = distance50Response.body()

                    if (distanceBody != null) {
                        distance50CardList.addAll(distanceBody.embedded.distanceCardDtoList)
                    } else {
                        Log.d("MainActivity", "distance body is null")
                    }
                } else {
                    Log.d("MainActivity", "getDistanceCardList fail")
                }
            }
        }
        catch (e: Exception) {
            Log.e("HomeViewModel", e.printStackTrace().toString())
        }
    }
}
