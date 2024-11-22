package com.sooum.android.data.remote

import com.sooum.android.SooumApplication
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = SooumApplication().getVariable("accessToken").toString()

        // Access Token이 null이면 헤더를 추가하지 않음
        val modifiedRequest = accessToken?.let {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $it")
                .build()
        } ?: chain.request()

        return chain.proceed(modifiedRequest)
    }
}