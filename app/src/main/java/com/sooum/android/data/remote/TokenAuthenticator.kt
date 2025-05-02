package com.sooum.android.data.remote

import android.util.Log
import com.sooum.android.Constants
import com.sooum.android.SooumApplication
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject

class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.e("asd","새 토큰 발급 시도")
        if (responseCount(response) >= 2) return null

        //  refreshToken
        val refreshToken = SooumApplication().getVariable("refreshToken")

        // 새 토큰 받아오기
        val newToken = getNewAccessToken(refreshToken) ?: return null

        // 저장
        SooumApplication().saveVariable("accessToken",newToken)

        // 재 시도
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")     // addHeader → header
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

    private fun getNewAccessToken(refreshToken: String?): String? {
        if (refreshToken.isNullOrBlank()) return null

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("${Constants.BASE_URL}/users/token")
            .addHeader("Authorization", "Bearer $refreshToken")
            .post(RequestBody.create(null, ByteArray(0)))
            .build()

        return try {
            client.newCall(request).execute().use { res ->
                val raw = res.body?.string().orEmpty()

                // 새토큰 발급 200
                if (res.isSuccessful) {
                    val json = JSONObject(raw)
                    json.optString("accessToken", null)
                } else {
                    // 로그인 처리 필요
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("refresh", "exception: ${e.message}")
            null
        }
    }
}
