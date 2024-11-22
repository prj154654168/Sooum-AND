package com.sooum.android.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.RetrofitInterface
import com.sooum.android.domain.model.EncryptedDeviceId
import kotlinx.coroutines.launch
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

class LogInViewModel : ViewModel() {
    val retrofitInstance = RetrofitInterface.getInstance().create(CardApi::class.java)
    var key by mutableStateOf<String?>(null)

    @RequiresApi(Build.VERSION_CODES.O)
    fun base64ToRSAPublicKey(base64Key: String): PublicKey {
        // Base64 문자열을 디코딩
        val keyBytes = Base64.getDecoder().decode(base64Key)

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

        val publicKey = base64ToRSAPublicKey(key.toString())
        // 문자열 암호화
        return encryptWithRSAPublicKey(android_id, publicKey)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun login(android_id: String) {
        viewModelScope.launch {
            try {
                val a = retrofitInstance.getRsaKey()
                key = a.body()!!.publicKey
                Log.e("getRsaKey", key.toString())
                val b = retrofitInstance.logIn(EncryptedDeviceId(convert(android_id)))
                Log.e("convert", convert(android_id).toString())
                Log.e("convert", b.toString())
            } catch (E: Exception) {
                println(E)
            }
        }
    }


}