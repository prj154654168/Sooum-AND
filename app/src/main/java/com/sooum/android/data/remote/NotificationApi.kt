package com.sooum.android.data.remote

import com.sooum.android.domain.model.UnreadCountDataModel
import retrofit2.Response
import retrofit2.http.GET

interface NotificationApi {
    @GET("/notifications/unread-cnt")
    suspend fun getAllUnreadCount(
    ): Response<UnreadCountDataModel>

    @GET("/notifications/card/unread-cnt")
    suspend fun getCardUnreadCount(
    ): Response<UnreadCountDataModel>

    @GET("/notifications/like/unread-cnt")
    suspend fun getLikeUnreadCount(
    ): Response<UnreadCountDataModel>
}