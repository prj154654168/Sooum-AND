package com.sooum.android.data.remote

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.sooum.android.R
import com.sooum.android.SooumApplication
import com.sooum.android.ui.MainActivity


class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO 새로운 토큰 수신 시 서버로 전송
        Log.e("fcmToken", token.toString())
        SooumApplication().saveVariable("fcmToken", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.e("From", "${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Message Notification", "Message data payload: ${remoteMessage.data}")
        }
        val targetCardId = remoteMessage.data["targetCardId"]
        val notificationId = remoteMessage.data["notificationId"]
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("targetCardId", targetCardId)
            putExtra("notificationId", notificationId)
        }

        val mainPendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val data = remoteMessage.data
        val gson = Gson()
        val notificationJson = gson.toJson(remoteMessage.data)
        Log.e("Notification JSON", notificationJson)
        val messageTitle: String = remoteMessage.data["title"].toString()
        val messageContent: String = remoteMessage.data["body"].toString()

        val builder1 = NotificationCompat.Builder(this, "sooum-channel")
            .setSmallIcon(R.drawable.sooum_logo)
            .setContentTitle(messageTitle)
            .setContentText(messageContent)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)
            .setFullScreenIntent(mainPendingIntent, true)
            .build()

//        NotificationManagerCompat.from(this).apply {
//            notify(101, builder1.build())
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                with(NotificationManagerCompat.from(this)) {
                    notify(0, builder1)
                }
            } else {
            }
        } else {
            // Android 13 미만에서는 권한 요청 필요 없음
            with(NotificationManagerCompat.from(this)) {
                notify(0, builder1)
            }
        }
    }
}