package com.sooum.android.domain.repository

import androidx.paging.PagingData
import com.sooum.android.domain.model.NotificationDataModel
import com.sooum.android.domain.model.NotificationResponse
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getAllUnreadCount() : Int

    suspend fun getCardUnreadCount() : Int

    suspend fun getLikeUnreadCount() : Int

    fun getAllUnreadNotification(): Flow<PagingData<NotificationDataModel>>

    fun getCardUnreadNotification(): Flow<PagingData<NotificationDataModel>>

    fun getLikeUnreadNotification(): Flow<PagingData<NotificationDataModel>>

    fun getAllReadNotification(): Flow<PagingData<NotificationDataModel>>

    fun getCardReadNotification(): Flow<PagingData<NotificationDataModel>>

    fun getLikeReadNotification(): Flow<PagingData<NotificationDataModel>>

    suspend fun patchNotificationRead(notificationId: Long): Int
}