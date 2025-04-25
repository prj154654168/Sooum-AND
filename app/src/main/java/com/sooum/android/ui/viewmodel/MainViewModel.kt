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
import com.sooum.android.domain.usecase.user.PostLoginUseCase
import com.sooum.android.domain.usecase.user.PostMemberSuspensionUseCase
import com.sooum.android.domain.usecase.user.RsaKeyUseCase
import com.sooum.android.domain.usecase.version.AppVersionUseCase
import com.sooum.android.enums.UserStatusEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.util.Base64
import javax.crypto.Cipher
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllUnreadCountUseCase: AllUnreadCountUseCase,
    private val readNotificationUseCase: ReadNotificationUseCase,
    private val suspensionUseCase: SuspensionUseCase,
    private val getAppVersionUseCase: AppVersionUseCase,
    private val getRsaKeyUseCase: RsaKeyUseCase,
    private val postLoginUseCase: PostLoginUseCase,
    private val postMemberSuspensionUseCase: PostMemberSuspensionUseCase
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
    fun refactConvert(androidId: String, rsaKey: String): String {
        val publicKey = base64ToRSAPublicKey(rsaKey)
        Log.d("MainViewModel", "publicKey : ${publicKey}")

        return encryptWithRSAPublicKey(androidId, publicKey)
    }

    suspend fun getRsaKey(): String {
        return withContext(Dispatchers.IO) {
            try {
                val keyModel = getRsaKeyUseCase()
                keyModel.publicKey
            } catch (e: Exception) {
                println(e)
                ""
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refactLogin(androidId: String, onLoginFinished: (UserStatusEnum, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val accessToken = SooumApplication().getVariable("accessToken")
                val refreshToken = SooumApplication().getVariable("refreshToken")

                Log.d("MainViewModel", "accessToken : $accessToken")
                Log.d("MainViewModel", "refreshToken : $refreshToken")

                if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                    Log.d("MainViewModel", "token is not empty")
                    onLoginFinished(UserStatusEnum.MEMBER, null)
                } else {
                    val rsaKey = getRsaKey()

                    Log.d("MainViewModel", "rsaKey : ${rsaKey}")

                    val encryptedDeviceId = refactConvert(androidId, rsaKey)

                    Log.d("MainViewModel", "android ID : ${androidId}")

                    Log.d("MainViewModel", "encryptedDeviceId : ${encryptedDeviceId}")

                    val tryLogin = postLoginUseCase(encryptedDeviceId)

                    Log.d("tryLogin", "${tryLogin}")

                    if (tryLogin.isRegistered) {
                        //토큰 저장하고 바로 메인화면으로 넘어가면 됨
                        val accessToken = tryLogin.token?.accessToken ?: ""
                        val refreshToken = tryLogin.token?.refreshToken ?: ""
                        SooumApplication().saveVariable("accessToken", accessToken)
                        SooumApplication().saveVariable("refreshToken", refreshToken)
                        onLoginFinished(UserStatusEnum.MEMBER, null)
                    } else {
                        //이 유저 가입 가능한 유저인지 판단하고 3, 4에 따라 dialog 띄우고 만약 가입 가능한 유저면 온보딩 화면으로 넘어가면 됨
                        val suspensionResponse = postMemberSuspensionUseCase(encryptedDeviceId)

                        if (suspensionResponse.isBanUser) {
                            onLoginFinished(UserStatusEnum.SUSPENDED, suspensionResponse.untilBan)
                            //밴유저임 정지 팝업 띄우면됨
                        } else {
                            if (suspensionResponse.status.responseMessage == "가입 가능한 유저입니다.") {
                                onLoginFinished(UserStatusEnum.NON_MEMBER, null)
                                //가입 가능, 온보딩 화면 이동
                            } else {
                                onLoginFinished(
                                    UserStatusEnum.RESTRICTED,
                                    suspensionResponse.untilBan
                                )
                                //탈퇴 유저임 재가입 불가 팝업 띄우면됨
                            }

                        }
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
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
                        onLoginFinished()
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

    suspend fun checkAppVersion(context: Context): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val info = context.packageManager
                    .getPackageInfo(context.packageName, 0)
                getAppVersionUseCase(info.versionName)
            }
                .mapCatching { it.trim('"') }
                .onFailure { Log.e("VersionCheck", it.message.orEmpty()) }
                .getOrNull() == "UPDATE"
        }
}
