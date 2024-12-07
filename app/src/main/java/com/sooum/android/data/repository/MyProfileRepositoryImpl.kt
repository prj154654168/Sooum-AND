package com.sooum.android.data.repository

import com.sooum.android.data.remote.ProfileApi
import com.sooum.android.domain.model.CodeDataModel
import com.sooum.android.domain.model.MyCommentCardDataModel
import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.model.MyProfileDataModel
import com.sooum.android.domain.model.NoticeDataModel
import com.sooum.android.domain.model.UserCodeBody
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

    override suspend fun getMyCommentCard(): List<MyCommentCardDataModel.MyCommentCardDto> {
        val response = profileApi.getMyCommentCard()

        if (response.isSuccessful) {
            return if (response.code() == 204) {
                emptyList()
            } else {
                response.body()!!._embedded.myCommentCardDtoList
            }
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun getNotice(): List<NoticeDataModel.NoticeDto> {
        val response = profileApi.getNotice()

        if (response.isSuccessful) {
            return if (response.code() == 204) {
                emptyList()
            } else {
                response.body()!!._embedded.noticeDtoList
            }
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun deleteUser() {
        val response = profileApi.deleteUser()
    }

    override suspend fun getCode(): CodeDataModel {
        val response = profileApi.getCode()

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun patchCode(): CodeDataModel {
        val response = profileApi.patchCode()

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun postCode(userCodeBody: UserCodeBody) {
        val response = profileApi.postCode(
            userCodeBody
        )
    }
}
