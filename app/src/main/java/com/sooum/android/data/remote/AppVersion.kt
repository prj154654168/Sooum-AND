package com.sooum.android.data.remote


import okhttp3.ResponseBody
import retrofit2.http.GET

interface AppVersionApi {
    @GET("/app/version/android")
    suspend fun getAppVersion(
    ): ResponseBody
}
