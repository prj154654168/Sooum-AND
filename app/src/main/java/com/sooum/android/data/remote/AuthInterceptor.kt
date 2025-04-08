package com.sooum.android.data.remote

import android.util.Log
import com.sooum.android.Constants
import com.sooum.android.SooumApplication
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = SooumApplication().getVariable("accessToken")
        val newRequest = chain.request().newBuilder().apply {
            if (accessToken.isNotEmpty()) {
                addHeader("Authorization", "Bearer $accessToken")
            }
        }.build()
        return chain.proceed(newRequest)
    }
}
