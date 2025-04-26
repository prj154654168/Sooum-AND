package com.sooum.android.data.remote

import android.util.Log
import com.sooum.android.Constants
import com.sooum.android.SooumApplication
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AuthInterceptor : Interceptor {

    private val excludedPaths = listOf(
        "/users",
        "/profiles"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = SooumApplication().getVariable("accessToken")
        val request = chain.request()
        val path = request.url.encodedPath

        val shouldExclude = excludedPaths.any { path.startsWith(it) }

        return if (shouldExclude) {
            chain.proceed(request)
        } else {
            val newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(newRequest)
        }

//        val newRequest = chain.request().newBuilder().apply {
//            if (accessToken.isNotEmpty()) {
//                addHeader("Authorization", "Bearer $accessToken")
//            }
//        }.build()
//        return chain.proceed(newRequest)
    }
}
