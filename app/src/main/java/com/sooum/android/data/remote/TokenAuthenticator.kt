package com.sooum.android.data.remote

import android.util.Log
import com.sooum.android.Constants
import com.sooum.android.SooumApplication
import okhttp3.Authenticator
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject

class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator", "start authenticate")
        // 2번 이상 재시도 방지
        if (responseCount(response) >= 2) return null

        val refreshToken = SooumApplication().getVariable("refreshToken")
        val newToken = getNewAccessToken(refreshToken) ?: return null

        Log.d("AuthInterceptor", "AccessToken: $newToken")
        // 새 토큰 저장
        SooumApplication().saveVariable("accessToken", newToken)

        return response.request.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $newToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private fun getNewAccessToken(refreshToken: String): String? {
        val client = OkHttpClient() // interceptor 없는 전용 클라이언트
        val request = Request.Builder()
            .url("${Constants.BASE_URL}/users/token")
            .post(FormBody.Builder().add("refresh_token", refreshToken).build())
            .build()
        return try {
            val response = client.newCall(request).execute()
            // 응답 코드부터 확인

            val body = response.body?.string()

            if (response.isSuccessful && !body.isNullOrEmpty()) {
                val json = JSONObject(body)
                val statusCode = json.getJSONObject("status").getInt("code")
                Log.e("TokenAuthenticator","asdState: $statusCode")
                if (statusCode == 200) json.getString("accessToken") else null
            } else null
        } catch (e: Exception) {
            Log.e("TokenAuthenticator","새토큰 발급 에러 발생 : ${e.message}")
            null
        }
    }
}
