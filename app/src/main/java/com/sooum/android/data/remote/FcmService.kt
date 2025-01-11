package com.sooum.android.data.remote

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.sooum.android.SooumApplication
import com.sooum.android.ui.MainActivity


class FcmService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO 새로운 토큰 수신 시 서버로 전송
        Log.e("fcmToken", token.toString())
        SooumApplication().saveVariable("fcmToken", token)
    }

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val messageTitle: String
        val messageContent: String
        Log.e("From", "${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Message Notification", "Message data payload: ${remoteMessage.data}")
        }
        val targetCardId = remoteMessage.data["targetCardId"]
        val notificationId = remoteMessage.data["notificationId"]
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("targetCardId", targetCardId)
            putExtra("notificationId", notificationId)
        }
        val mainPendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        if (remoteMessage.notification != null) { // notification이 있는 경우 foreground처리
            //foreground
            messageTitle = remoteMessage.notification!!.title.toString()
            messageContent = remoteMessage.notification!!.body.toString()
            val gson = Gson()
            val notificationJson = gson.toJson(remoteMessage.notification)
            Log.e("Notification JSON", notificationJson)
        } else {  // background 에 있을경우 혹은 foreground에 있을경우 두 경우 모두
            val data = remoteMessage.data
            val gson = Gson()
            val notificationJson = gson.toJson(remoteMessage.data)
            Log.e("Notification JSON", notificationJson)
            messageTitle = data["title"].toString()
            messageContent = data["body"].toString()
        }

        val builder1 = NotificationCompat.Builder(this, "sooum-channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(messageTitle)
            .setContentText(messageContent)
            .setAutoCancel(true)
            .setContentIntent(mainPendingIntent)
            .setFullScreenIntent(mainPendingIntent, true)

//        NotificationManagerCompat.from(this).apply {
//            notify(101, builder1.build())
//        }
        with(NotificationManagerCompat.from(this)) {
            notify(101, builder1.build())
        }
    }


    /*@RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("From", "${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Message Notification", "Message data payload: ${remoteMessage.data}")
        }
        val targetCardId = remoteMessage.data["targetCardId"]
        val notificationId = remoteMessage.data["notificationId"]
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("targetCardId", targetCardId)
            putExtra("notificationId", notificationId)
        }
//        val mainIntent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
        val mainPendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        // 알림 메시지인 경우
        if (remoteMessage.notification != null) {
            Log.e("Message Notification Body", "${remoteMessage.notification?.body}")
            //Log.e("Message Notification Body",remoteMessage.notification.)
            val gson = Gson()
            val notificationJson = gson.toJson(remoteMessage.notification)
            Log.e("Notification JSON", notificationJson)
            showNotification(
                remoteMessage.notification?.title,
                remoteMessage.notification?.body,
                mainPendingIntent
            )
        }

    }*/

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(title: String?, body: String?, mainPendingIntent: PendingIntent) {
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
            .setContentIntent(mainPendingIntent)
            .setFullScreenIntent(mainPendingIntent, true)
        notificationManager.notify(101, notificationBuilder.build())
        NotificationManagerCompat.from(this).apply {
            notify(101, notificationBuilder.build())
        }
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