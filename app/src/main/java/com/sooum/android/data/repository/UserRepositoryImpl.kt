package com.sooum.android.data.repository

import android.util.Log
import com.sooum.android.data.remote.UserApi
import com.sooum.android.domain.model.EncryptedDeviceId
import com.sooum.android.domain.model.KeyModel
import com.sooum.android.domain.model.LoginModel
import com.sooum.android.domain.model.RecommendTagDataModel
import com.sooum.android.domain.model.RefreshTokenResponseModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.model.SuspensionResponse
import com.sooum.android.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userApi: UserApi) : UserRepository {
    override suspend fun getRsaKey(): KeyModel {
        return try {
            val response = userApi.getRsaKey()

            if (response.isSuccessful) {
                response.body() ?: throw  Exception()
            } else {
                throw Exception()
            }
        } catch (e: Exception) {
            throw Exception()
        }
    }

    override suspend fun postLogin(encryptedDeviceId: String): LoginModel {
        return try {
            val response = userApi.postLogin(EncryptedDeviceId(encryptedDeviceId))

            Log.d("MainViewModel", "LoginResponse : ${response}")

            if (response.isSuccessful) {

                response.body() ?: throw  Exception()
            } else {
                throw Exception()
            }
        } catch (e: Exception) {
            throw Exception()
        }
    }

    override suspend fun postMemberSuspension(encryptedDeviceId: String): SuspensionResponse {
        return try {
            val response = userApi.postMemberSuspension(EncryptedDeviceId(encryptedDeviceId))

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    response.body() ?: throw  Exception()
                } else if (response.code() == 204) {
                    SuspensionResponse(
                        untilBan = "",
                        isBanUser = false,
                        status = Status(
                            httpCode = 204,
                            httpStatus = "NO CONTENT",
                            responseMessage = "가입 가능한 유저입니다."  // 임의의 메시지 추가
                        ))
                } else {
                    throw Exception()
                }

            } else {
                throw Exception()
            }
        } catch (e: Exception) {
            throw Exception()
        }
    }

    override fun postRefreshToken(): RefreshTokenResponseModel {
        try {
            val response = userApi.postRefreshToken()

            if (response.isSuccessful && response.code() == 200) {
                return response.body() ?: throw Exception()
            } else {
                throw Exception()
            }
        } catch (e: Exception) {
            throw Exception()
        }
    }
}