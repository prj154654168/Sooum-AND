package com.sooum.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.sooum.android.data.remote.AuthInterceptor
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@HiltAndroidApp
class SooumApplication : Application() {
    companion object {
        private val prefsFilename = "Prefs"
        lateinit var prefs: SharedPreferences
        lateinit var retrofitInstance: Retrofit
    }

    fun saveVariable(item: String, data: String) {
        prefs.edit().putString(item, data).apply()
    }

    fun getVariable(item: String): String {
        return prefs.getString(item, "").toString()
    }

    fun removeVariable(item: String) {
        prefs.edit().remove(item).apply()
    }

    fun clearAllPrefs() {
        prefs.edit().clear().apply()
    }


    private fun getPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        prefs = getPreference(applicationContext)
        initialize()
        createNotificationChannel()

    }

    fun initialize() {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 요청/응답 본문까지 출력
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor()) // Context 전달
            .build()

        retrofitInstance = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient) // OkHttpClient 사용
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val instance: Retrofit
        get() = retrofitInstance

    // 채널 생성
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "high_priority_channel"
            val channelName = "High Priority Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Heads-up 알림용 채널"
                enableVibration(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            Log.d("FCM", "high_priority_channel 채널 생성됨")
        }
    }
}