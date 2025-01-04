package com.sooum.android.data.remote

import com.sooum.android.domain.model.NotificationDataModel
import com.sooum.android.domain.model.NotificationResponse
import com.sooum.android.domain.model.UnreadCountDataModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

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

    @GET("/notifications/unread")
    suspend fun getUnreadAllNotifications(
    ): Response<List<NotificationResponse>>

    @GET("/notifications/unread/{lastId}")
    suspend fun getUnreadAllNotificationsAfter(
        @Path("lastId") lastId: Long
    ): Response<List<NotificationResponse>>

    @GET("/notifications/like/unread")
    suspend fun getUnreadLikeNotifications(
    ): Response<List<NotificationResponse>>

    @GET("/notifications/like/unread/{lastId}")
    suspend fun getUnreadLikeNotificationsAfter(
        @Path("lastId") lastId: Long
    ): Response<List<NotificationResponse>>

    @GET("/notifications/card/unread")
    suspend fun getUnreadCardNotifications(
    ): Response<List<NotificationResponse>>

    @GET("/notifications/card/unread/{lastId}")
    suspend fun getUnreadCardNotificationsAfter(
        @Path("lastId") lastId: Long
    ): Response<List<NotificationResponse>>

    @GET("/notifications/read")
    suspend fun getReadAllNotifications(
    ): Response<List<NotificationResponse>>

    @GET("/notifications/read/{lastId}")
    suspend fun getReadAllNotificationsAfter(
        @Path("lastId") lastId: Long
    ): Response<List<NotificationResponse>>

    @GET("/notifications/like/read")
    suspend fun getReadLikeNotifications(
    ): Response<List<NotificationResponse>>

    @GET("/notifications/like/read/{lastId}")
    suspend fun getReadLikeNotificationsAfter(
        @Path("lastId") lastId: Long
    ): Response<List<NotificationResponse>>

    @GET("/notifications/card/read")
    suspend fun getReadCardNotifications(
    ): Response<List<NotificationResponse>>

    @GET("/notifications/card/read/{lastId}")
    suspend fun getReadCardNotificationsAfter(
        @Path("lastId") lastId: Long
    ): Response<List<NotificationResponse>>

    @PATCH("/notifications/{notificationId}/read")
    suspend fun patchNotificationRead(
        @Path("notificationId") notificationId: Long
    ): Response<Unit>
}