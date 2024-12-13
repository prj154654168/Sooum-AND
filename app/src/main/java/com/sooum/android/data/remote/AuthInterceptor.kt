package com.sooum.android.data.remote

import com.sooum.android.Constants
import com.sooum.android.SooumApplication
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = SooumApplication().getVariable("accessToken").toString()

        // Access Token이 null이면 헤더를 추가하지 않음
        val modifiedRequest = accessToken?.let {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $it")
//                .addHeader("Authorization", "Bearer ${Constants.ACCESS_TOKEN}")
                .build()
        } ?: originalRequest
        val initialResponse = chain.proceed(modifiedRequest)
        if (initialResponse.code == 401) {
            initialResponse.close() // 기존 응답 닫기

            val refreshToken = SooumApplication().getVariable("refreshToken").toString()
            if (refreshToken != "") {
                val newAccessToken = getNewAccessToken(refreshToken) // Access Token 갱신 로직
                if (newAccessToken != null) {
                    SooumApplication().saveVariable("accessToken", newAccessToken) // 새로운 토큰 저장

                    // 새로운 Access Token으로 요청 재시도
                    val retriedRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()
                    return chain.proceed(retriedRequest)
                }
            }
        }

        return initialResponse
    }
    private fun getNewAccessToken(refreshToken: String): String? {
        // 새로운 Access Token을 요청하는 네트워크 통신 로직
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${Constants.BASE_URL}/users/token") // 서버의 토큰 갱신 엔드포인트
            .post(
                FormBody.Builder()
                    .add("refresh_token", refreshToken)
                    .build()
            )
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (!responseBody.isNullOrEmpty()) {
                    // JSON 파싱하여 accessToken 추출
                    val jsonObject = JSONObject(responseBody)
                    val statusCode = jsonObject.getJSONObject("status").getInt("code")
                    if (statusCode == 200) {
                        jsonObject.getString("accessToken") // Access Token 반환
                    } else {
                        null // 갱신 실패
                    }
                } else {
                    null // 빈 응답 처리
                }
            } else {
                null // 응답 실패 처리
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // 예외 발생 시 null 반환
        }
    }
}