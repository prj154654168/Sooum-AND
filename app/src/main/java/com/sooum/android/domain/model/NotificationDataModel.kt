package com.sooum.android.domain.model

import com.sooum.android.enums.NotificationTypeEnum

sealed class NotificationDataModel(
    open val notificationId: Long,          // 공통 속성
    open val notificationType: NotificationTypeEnum // 공통 속성
) {
    data class DeletedNotification(
        override val notificationId: Long,
        override val notificationType: NotificationTypeEnum,
        val createTime: String
    ) : NotificationDataModel(notificationId, notificationType)

    data class BlockedNotification(
        override val notificationId: Long,
        override val notificationType: NotificationTypeEnum,
        val createTime: String,
        val blockExpirationDateTime: String? = null
    ) : NotificationDataModel(notificationId, notificationType)

    data class FeedLikeNotification(
        override val notificationId: Long,
        override val notificationType: NotificationTypeEnum,
        val createTime: String,
        val content: String,
        val font: String,
        val fontSize: String,
        val nickName: String,
        val imgUrl: String,
        val targetCardId: Long // 추가된 속성
    ) : NotificationDataModel(notificationId, notificationType)

    data class CommentLikeNotification(
        override val notificationId: Long,
        override val notificationType: NotificationTypeEnum,
        val createTime: String,
        val content: String,
        val font: String,
        val fontSize: String,
        val nickName: String,
        val imgUrl: String,
        val targetCardId: Long // 추가된 속성
    ) : NotificationDataModel(notificationId, notificationType)

    data class CommentWriteNotification(
        override val notificationId: Long,
        override val notificationType: NotificationTypeEnum,
        val createTime: String,
        val content: String,
        val font: String,
        val fontSize: String,
        val nickName: String,
        val imgUrl: String,
        val targetCardId: Long // 추가된 속성
    ) : NotificationDataModel(notificationId, notificationType)
}


data class NotificationResponse(
    val notificationId: Long,
    val notificationType: String,
    val createTime: String,
    val blockExpirationDateTime: String? = null,
    val content: String? = null,
    val font: String? = null,
    val fontSize: String? = null,
    val nickName: String? = null,
    val imgUrl: Map<String, String>? = null, // Nested JSON 처리
    val targetCardId: Long? = null // 추가된 속성
)


