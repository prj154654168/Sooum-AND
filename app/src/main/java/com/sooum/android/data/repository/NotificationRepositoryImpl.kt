package com.sooum.android.data.repository

import com.sooum.android.data.remote.NotificationApi
import com.sooum.android.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(private val notificationApi: NotificationApi) : NotificationRepository {
    override suspend fun getAllUnreadCount(): Int {
        val response = notificationApi.getAllUnreadCount()

        if (response.isSuccessful) {
            return response.body()?.unreadCnt ?: 0
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun getCardUnreadCount(): Int {
        val response = notificationApi.getCardUnreadCount()

        if (response.isSuccessful) {
            return response.body()?.unreadCnt ?: 0
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }

    override suspend fun getLikeUnreadCount(): Int {
        val response = notificationApi.getLikeUnreadCount()

        if (response.isSuccessful) {
            return response.body()?.unreadCnt ?: 0
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }
}