package com.sooum.android.domain.model

data class UserCodeBody(
    val deviceType: String,
    val transferId: String,
    val encryptedDeviceId: String,
)
