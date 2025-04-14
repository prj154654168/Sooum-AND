package com.sooum.android.domain.repository

import com.sooum.android.domain.model.EncryptedDeviceId
import com.sooum.android.domain.model.KeyModel
import com.sooum.android.domain.model.LoginModel
import com.sooum.android.domain.model.RefreshTokenResponseModel
import com.sooum.android.domain.model.SuspensionResponse

interface UserRepository {
    suspend fun getRsaKey() : KeyModel

    suspend fun postLogin(encryptedDeviceId: String) : LoginModel

    suspend fun postMemberSuspension(encryptedDeviceId: String) : SuspensionResponse

    fun postRefreshToken(): RefreshTokenResponseModel
}