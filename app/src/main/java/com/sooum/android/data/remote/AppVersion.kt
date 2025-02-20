package com.sooum.android.data.remote


import retrofit2.http.GET

interface AppVersionApi {
    @GET("/app/version/android")
    suspend fun getAppVersion(
    ): String
}
