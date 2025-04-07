package com.sooum.android.ui.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.SooumApplication
import com.sooum.android.data.remote.CardApi
import com.sooum.android.domain.model.EncryptedDeviceId
import com.sooum.android.domain.model.Token
import com.sooum.android.domain.usecase.notification.AllUnreadCountUseCase
import com.sooum.android.domain.usecase.notification.ReadNotificationUseCase
import com.sooum.android.domain.usecase.profile.SuspensionUseCase
import com.sooum.android.domain.usecase.version.AppVersionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.util.Base64
import javax.crypto.Cipher
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllUnreadCountUseCase: AllUnreadCountUseCase,
    private val readNotificationUseCase: ReadNotificationUseCase,
    private val suspensionUseCase: SuspensionUseCase,
    private val getAppVersionUseCase: AppVersionUseCase
) : ViewModel() {
    val retrofitInstance = SooumApplication().instance.create(CardApi::class.java)
    var key by mutableStateOf<String?>(null)
    var login by mutableStateOf<Int>(0)
    var token: Token? = null
    var encryptedDeviceId: String = ""
    var isLoading by mutableIntStateOf(0)
    var date by mutableStateOf("")

    var unreadNotificationCount = mutableStateOf(0)
        private set

    // 버전 비교용 다이얼로그
    val showDialogVersion = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    fun base64ToRSAPublicKey(base64Key: String): PublicKey {
        // Base64 문자열에서 공백이나 줄바꿈 제거
        val cleanedKey = base64Key.replace("\n", "").replace("\r", "").trim()

        // Base64 문자열을 디코딩
        val keyBytes = Base64.getDecoder().decode(cleanedKey)

        // X509EncodedKeySpec을 생성하여 RSA Public Key로 변환
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePublic(keySpec)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptWithRSAPublicKey(plainText: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding") // RSA 알고리즘 사용
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encrypt = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(encrypt)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convert(android_id: String): String {
        try {
            val publicKey = base64ToRSAPublicKey(key.toString())
            // 문자열 암호화
            return encryptWithRSAPublicKey(android_id, publicKey)
        } catch (e: Exception) {
            Log.e("MainViewModel", "Encryption failed: ${e.message}")
            e.printStackTrace()
            return "" // 예외 발생 시 빈 문자열 반환
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun login(android_id: String, onLoginFinished: () -> Unit) {
        Log.e("android_id", android_id)
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "MainViewModel Start")
                val a = retrofitInstance.getRsaKey()

                if (a.isSuccessful) {
                    key = a.body()!!.publicKey
                    Log.d("MainViewModel", "${key}")
                    encryptedDeviceId = convert(android_id)
                    Log.d("MainViewModel", "${encryptedDeviceId}")
                    SooumApplication().saveVariable(
                        "encryptedDeviceId",
                        encryptedDeviceId
                    )

                    val suspension = suspensionUseCase(EncryptedDeviceId(encryptedDeviceId))
                    if (suspension != null) {
                        login = if (suspension.isBanUser) {
                            3
                        }//벤 당한사람
                        else {
                            4
                        }//아이디 탈퇴한사람
                        val dateTime = LocalDateTime.parse(suspension.untilBan)
                        date = "${dateTime.year}년 ${dateTime.monthValue}월 ${dateTime.dayOfMonth}일"
                    } else {
                        val b = retrofitInstance.logIn(EncryptedDeviceId(encryptedDeviceId))
                        Log.e("EncryptedDeviceId", b.body().toString())

                        if (b.body()?.isRegistered == true) {
                            login = 1
                            token = b.body()!!.token
                            token?.let {
                                SooumApplication().saveVariable(
                                    "accessToken",
                                    it.accessToken
                                )
                                SooumApplication().saveVariable(
                                    "refreshToken",
                                    it.refreshToken
                                )
                            }

                            onLoginFinished()
                        } else {
                            login = 2
                        }
                    }
                }

            } catch (E: Exception) {
                println(E)
            }
        }
    }

    fun fetchUnreadNotificationCount() {
        viewModelScope.launch {
            try {
                val unreadCount = getAllUnreadCountUseCase()
                unreadNotificationCount.value = unreadCount
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.printStackTrace().toString())
            }
        }
    }

    fun fetchAppVersion(context: Context) {
        viewModelScope.launch {
            runCatching {
                val packageManager = context.packageManager
                val packageName = context.packageName
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                getAppVersionUseCase(packageInfo.versionName)
            }
                .onSuccess { result ->
                    if (result == "\"UPDATE\"") {
                        // 업데이트 진행
                        showDialogVersion.value = true
                    }
                }
                .onFailure { error ->
                    Log.e("error ", "versionError : ${error.message}")
                    // 에러 방지로 실패시 그냥 통과
                }
        }
    }
}
