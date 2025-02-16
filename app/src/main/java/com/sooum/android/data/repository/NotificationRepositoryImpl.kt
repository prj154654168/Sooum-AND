package com.sooum.android.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sooum.android.data.paging.NotificationPagingSource
import com.sooum.android.data.remote.NotificationApi
import com.sooum.android.domain.model.NotificationDataModel
import com.sooum.android.domain.model.NotificationResponse
import com.sooum.android.domain.repository.NotificationRepository
import com.sooum.android.enums.NotificationEnum
import com.sooum.android.enums.NotificationTypeEnum
import kotlinx.coroutines.flow.Flow
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

    override fun getAllUnreadNotification(): Flow<PagingData<NotificationDataModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NotificationPagingSource(notificationApi, NotificationEnum.UNREADALL) }
        ).flow
    }

    override fun getCardUnreadNotification(): Flow<PagingData<NotificationDataModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NotificationPagingSource(notificationApi, NotificationEnum.UNREADCARD) }
        ).flow
    }

    override fun getLikeUnreadNotification(): Flow<PagingData<NotificationDataModel>> {
        Log.d("123", "실행됨")
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NotificationPagingSource(notificationApi, NotificationEnum.UNREADLIKE) }
        ).flow
    }

    override fun getAllReadNotification(): Flow<PagingData<NotificationDataModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NotificationPagingSource(notificationApi, NotificationEnum.READALL) }
        ).flow
    }

    override fun getCardReadNotification(): Flow<PagingData<NotificationDataModel>> {
        return Pager(
            config = PagingConfig(
                pageSize =25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NotificationPagingSource(notificationApi, NotificationEnum.READCARD) }
        ).flow
    }

    override fun getLikeReadNotification(): Flow<PagingData<NotificationDataModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NotificationPagingSource(notificationApi, NotificationEnum.READLIKE) }
        ).flow
    }

    override suspend fun patchNotificationRead(notificationId: Long): Int {
        val response = notificationApi.patchNotificationRead(notificationId)
        if (response.isSuccessful) {
            return response.code()
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }
}