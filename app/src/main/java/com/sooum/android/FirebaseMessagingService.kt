package com.sooum.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sooum.android.domain.usecase.postcard.UpdateFcmTokenUseCase
import com.sooum.android.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New FCM token: $token")

        // 업데이트 된 토큰 서버로 전송
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val accessToken = SooumApplication().getVariable("accessToken")
                if (accessToken != null) {
                    updateFcmTokenUseCase(token)
                }

            } catch (e: Exception) {
                Log.i("FCM", "FCM 토큰 업데이트 실패: ${e.message}")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data



        // 서버가 data payload 없이 보내는 경우 대응
        if (data.isEmpty()) {
            val title = message.notification?.title ?: "알림"
            val body = message.notification?.body ?: "내용이 없습니다"

            showNotification(
                title,
                body,
                type = "DEFAULT",     // 최소한의 타입으로 넘겨줌
                notificationId = null,
                targetCardId = null
            )
            return
        }

        // 일반 data 기반 알림 처리
        val type = data["notificationType"] ?: ""
        val notificationId = data["notificationId"]
        val targetCardId = data["targetCardId"]

        if (type == "TRANSFER_SUCCESS") {
            FcmEventBus.notifyTransferSuccess()
        }

        showNotification(
            data["title"] ?: message.notification?.title ?: "알림",
            data["body"] ?: message.notification?.body ?: "",
            type,
            notificationId,
            targetCardId
        )
    }


    private fun showNotification(
        title: String,
        message: String,
        type: String,
        notificationId: String?,
        targetCardId: String?
    ) {
        Log.i("FCM", "showNotification() 호출됨 - title: $title / body: $message")
        // Android 13 이상: 알림 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                Log.i("FCM", "알림 권한 없음 — 알림 생략")
                return
            }
        }

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "high_priority_channel"

        // 채널 생성 (IMPORTANCE_HIGH로 변경 + 이름 바꿔 재생성 유도)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                Log.i("FCM", "알림 채널 없음 → 새로 생성함")
            } else {
                Log.i("FCM", "기존 알림 채널 존재함")
            }
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "High Priority Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Heads-up 알림용 채널"
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        // 클릭 시 MainActivity 로 전달할 Intent
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra("fromPush", true)
            putExtra("notificationType", type)
            notificationId?.let   { putExtra("notificationId", it) }
            targetCardId?.let     { putExtra("targetCardId", it) }
        }

        // 2-2) PendingIntent 생성
        val piFlags = PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_IMMUTABLE
                else 0

        val pendingIntent = PendingIntent.getActivity(
            this,
            type.hashCode(),
            intent,
            piFlags
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_sooum_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.ic_sooum_logo
                )
            )
            .setContentTitle(title)
            .setContentText(message)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()



        notificationManager.notify(type.hashCode(), notification)
    }
}

// 계정 이관용
object FcmEventBus {
    val transferEventFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun notifyTransferSuccess() {
        transferEventFlow.tryEmit(Unit)
    }
}
