package com.sooum.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.sooum.android.data.remote.AuthInterceptor
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@HiltAndroidApp
class SooumApplication : Application() {
    companion object{
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

    private fun getPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        prefs = getPreference(applicationContext)
        initialize()
    }

    fun initialize() {
        Log.e("AuthInterceptor","2")
        val okHttpClient = OkHttpClient.Builder()
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
}