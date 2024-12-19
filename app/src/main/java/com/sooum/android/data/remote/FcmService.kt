package com.sooum.android.data.remote

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sooum.android.SooumApplication


class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO 새로운 토큰 수신 시 서버로 전송
        Log.e("fcmToken",token.toString())
        SooumApplication().saveVariable("fcmToken",token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("From","${remoteMessage.from}")

        // 알림 메시지인 경우
        if (remoteMessage.notification != null) {
            Log.e("Message Notification Body","${remoteMessage.notification?.body}")
            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(title: String?, body: String?) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "sooum",
            "Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, "sooum-channel")
            .setContentTitle(title ?: "Promotion Title")
            .setContentText(body ?: "Promotion Message")
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setAutoCancel(true)

        notificationManager.notify(101, notificationBuilder.build())
    }
//
//    @SuppressLint("MissingPermission")
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        val messageTitle: String
//        val messageContent: String
//
//        if (remoteMessage.notification != null) { // notification이 있는 경우 foreground처리
//            //foreground
//            messageTitle = remoteMessage.notification!!.title.toString()
//            messageContent = remoteMessage.notification!!.body.toString()
//
//        } else {  // background 에 있을경우 혹은 foreground에 있을경우 두 경우 모두
//            val data = remoteMessage.data
//
//            messageTitle = data["title"].toString()
//            messageContent = data["body"].toString()
//        }
//
//        val mainIntent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//
//        val mainPendingIntent: PendingIntent =
//            PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)
//
//        val builder1 = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setContentTitle(messageTitle)
//            .setContentText(messageContent)
//            .setAutoCancel(true)
//            .setContentIntent(mainPendingIntent)
//            .setFullScreenIntent(mainPendingIntent, true)
//
//        NotificationManagerCompat.from(this).apply {
//            notify(101, builder1.build())
//        }
//    }
}