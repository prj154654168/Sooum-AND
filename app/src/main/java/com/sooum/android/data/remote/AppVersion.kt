package com.sooum.android.data.remote


import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface AppVersionApi {
    @GET("/app/version/android/v2")
    suspend fun getAppVersion(
        @Query("version") version: String
    ): ResponseBody
}
