package com.sooum.android.data.remote

import android.util.Log
import com.sooum.android.Constants
import com.sooum.android.SooumApplication
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject

class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.e("asd", "새 토큰 발급 시도")
        if (responseCount(response) >= 2) return null

        //  refreshToken
        val refreshToken = SooumApplication().getVariable("refreshToken")

        // 새 토큰 받아오기
        val newToken = getNewAccessToken(refreshToken) ?: return null

        // 저장
        SooumApplication().saveVariable("accessToken", newToken)

        // 재 시도
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
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
            // okhttp4 이후 대응 코드 -> RequestBody.create(mediaType,byteArray)는 deprecate 될 예정
            .post(ByteArray(0).toRequestBody(null))
            .build()


        return try {
            client.newCall(request).execute().use { res ->
                val raw = res.body?.string().orEmpty()

                when (res.code) {
                    200 -> {
                        // 새 토큰 발급
                        JSONObject(raw).optString("accessToken", null)
                    }

                    403 -> {
                        // refreshToken이 만료됬을 경우
                        null
                    }

                    418 -> {
                        // refresh 토큰이 블랙리스트에 등록 됬을 경우
                        null
                    }

                    else -> {
                        // 500 이나 아예 실패 했을 경우
                        null
                    }
                }

            }

        } catch (e: Exception) {
            Log.e("refresh", "토큰 발급 에러 : ${e.message}")
            null
        }
    }
}
