package com.sooum.android.domain.repository

interface NotificationRepository {
    suspend fun getAllUnreadCount() : Int

    suspend fun getCardUnreadCount() : Int

    suspend fun getLikeUnreadCount() : Int
}