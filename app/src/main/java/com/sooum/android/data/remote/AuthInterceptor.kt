package com.sooum.android.data.remote


import com.sooum.android.SooumApplication
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = SooumApplication.prefs.getString("accessToken", null)

        val origin = chain.request()
        val req = if (accessToken != null && origin.header("Authorization") == null) {
            origin.newBuilder()
                .header("Authorization", "Bearer $accessToken")   // addHeader → header
                .build()
        } else origin

        return chain.proceed(req)
    }
}
