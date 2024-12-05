package com.sooum.android.data.repository

import com.sooum.android.data.remote.ProfileApi
import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.model.MyProfileDataModel
import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class MyProfileRepositoryImpl @Inject constructor(private val profileApi: ProfileApi) :
    MyProfileRepository {
    override suspend fun getMyProfile(): MyProfileDataModel {
        val response = profileApi.getMyProfile()

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun getMyFeedCard(): List<MyFeedCardDataModel.MyFeedCardDto> {
        val response = profileApi.getMyFeedCard()

        if (response.isSuccessful) {
            return if (response.code() == 204) {
                emptyList()
            } else {
                response.body()!!._embedded.myFeedCardDtoList
            }
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }
}
