package com.sooum.android.data.remote

import com.sooum.android.domain.model.EncryptedDeviceId
import com.sooum.android.domain.model.KeyModel
import com.sooum.android.domain.model.LoginModel
import com.sooum.android.domain.model.RefreshTokenResponseModel
import com.sooum.android.domain.model.SuspensionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @GET("/users/key")
    suspend fun getRsaKey(
    ): Response<KeyModel>

    @POST("/users/login")
    suspend fun postLogin(
        @Body encryptedDeviceId: EncryptedDeviceId,
    ): Response<LoginModel>

    @POST("/members/suspension")
    suspend fun postMemberSuspension(
        @Body encryptedDeviceId: EncryptedDeviceId
    ) : Response<SuspensionResponse>


    @POST("/users/token")
    fun postRefreshToken() : Response<RefreshTokenResponseModel>
}