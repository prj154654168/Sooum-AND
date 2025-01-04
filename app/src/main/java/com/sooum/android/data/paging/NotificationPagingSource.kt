package com.sooum.android.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sooum.android.data.remote.NotificationApi
import com.sooum.android.domain.model.NotificationDataModel
import com.sooum.android.domain.model.NotificationResponse
import com.sooum.android.enums.NotificationEnum
import com.sooum.android.enums.NotificationTypeEnum
import javax.inject.Inject

class NotificationPagingSource @Inject constructor(
    private val apiService: NotificationApi,
    private val notificationType: NotificationEnum
) : PagingSource<Long, NotificationDataModel>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, NotificationDataModel> {
        return try {
            val lastNotificationId = params.key
            Log.d("123", "LoadParams Key: $lastNotificationId")

            val response = when (notificationType) {
                NotificationEnum.UNREADALL -> {
                    if (lastNotificationId == null) {
                        apiService.getUnreadAllNotifications()
                    } else {
                        apiService.getUnreadAllNotificationsAfter(lastNotificationId)
                    }
                }
                NotificationEnum.UNREADCARD -> {
                    if (lastNotificationId == null) {
                        apiService.getUnreadCardNotifications()
                    } else {
                        apiService.getUnreadCardNotificationsAfter(lastNotificationId)
                    }
                }
                NotificationEnum.UNREADLIKE -> {
                    if (lastNotificationId == null) {
                        apiService.getUnreadLikeNotifications()
                    } else {
                        apiService.getUnreadLikeNotificationsAfter(lastNotificationId)
                    }
                }
                NotificationEnum.READALL -> {
                    if (lastNotificationId == null) {
                        apiService.getReadAllNotifications()
                    } else {
                        apiService.getReadAllNotificationsAfter(lastNotificationId)
                    }
                }
                NotificationEnum.READCARD -> {
                    if (lastNotificationId == null) {
                        apiService.getReadCardNotifications()
                    } else {
                        apiService.getReadCardNotificationsAfter(lastNotificationId)
                    }
                }
                NotificationEnum.READLIKE -> {
                    if (lastNotificationId == null) {
                        apiService.getReadLikeNotifications()
                    } else {
                        apiService.getReadLikeNotificationsAfter(lastNotificationId)
                    }
                }
            }

            if (response.isSuccessful) {
                Log.d("123", "Response Code: ${response.code()}")
                if (response.code() == 204) {
                    Log.d("123", "No content (204)")
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                }

                val body = response.body()
                if (body.isNullOrEmpty()) {
                    Log.d("123", "200 - Response body is empty")
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                }

                val notifications = body.map { mapToNotificationDataModel(it) }
                Log.d("123", "200 - Notifications size: ${notifications.size}")
                return LoadResult.Page(
                    data = notifications,
                    prevKey = null,
                    nextKey = notifications.lastOrNull()?.notificationId
                )
            } else {
                Log.e("123", "API Error: ${response.code()}")
                return LoadResult.Error(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("123", "Load Error", e)
            return LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<Long, NotificationDataModel>): Long? {
        return null
    }

    private fun mapToNotificationDataModel(response: NotificationResponse): NotificationDataModel {
        Log.d("123", "실행됨2")
        val typeEnum = NotificationTypeEnum.valueOf(response.notificationType)
        return when (typeEnum) {
            NotificationTypeEnum.DELETED -> NotificationDataModel.DeletedNotification(
                notificationId = response.notificationId,
                notificationType = typeEnum,
                createTime = response.createTime
            )
            NotificationTypeEnum.BLOCKED -> NotificationDataModel.BlockedNotification(
                notificationId = response.notificationId,
                notificationType = typeEnum,
                createTime = response.createTime,
                blockExpirationDateTime = response.blockExpirationDateTime
            )
            NotificationTypeEnum.FEED_LIKE -> NotificationDataModel.FeedLikeNotification(
                notificationId = response.notificationId,
                notificationType = typeEnum,
                createTime = response.createTime,
                content = response.content ?: "",
                font = response.font ?: "",
                fontSize = response.fontSize ?: "",
                nickName = response.nickName ?: "",
                imgUrl = response.imgUrl?.get("href") ?: "",
                targetCardId = response.targetCardId ?: throw IllegalArgumentException("FEED_LIKE must have targetCardId")
            )
            NotificationTypeEnum.COMMENT_LIKE -> NotificationDataModel.CommentLikeNotification(
                notificationId = response.notificationId,
                notificationType = typeEnum,
                createTime = response.createTime,
                content = response.content ?: "",
                font = response.font ?: "",
                fontSize = response.fontSize ?: "",
                nickName = response.nickName ?: "",
                imgUrl = response.imgUrl?.get("href") ?: "",
                targetCardId = response.targetCardId ?: throw IllegalArgumentException("COMMENT_LIKE must have targetCardId")
            )
            NotificationTypeEnum.COMMENT_WRITE -> NotificationDataModel.CommentWriteNotification(
                notificationId = response.notificationId,
                notificationType = typeEnum,
                createTime = response.createTime,
                content = response.content ?: "",
                font = response.font ?: "",
                fontSize = response.fontSize ?: "",
                nickName = response.nickName ?: "",
                imgUrl = response.imgUrl?.get("href") ?: "",
                targetCardId = response.targetCardId ?: throw IllegalArgumentException("COMMENT_WRITE must have targetCardId")
            )
        }
    }
}
