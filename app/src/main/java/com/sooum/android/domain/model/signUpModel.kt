package com.sooum.android.domain.model

data class signUpModel(
    val memberInfo: MemberInfo,
    val policy: Policy
)
data class MemberInfo(
    val deviceType: String,
    val encryptedDeviceId: String,
    val firebaseToken: String,
    val isAllowNotify: Boolean
)
data class Policy(
    val isAllowTermOne: Boolean,
    val isAllowTermThree: Boolean,
    val isAllowTermTwo: Boolean
)