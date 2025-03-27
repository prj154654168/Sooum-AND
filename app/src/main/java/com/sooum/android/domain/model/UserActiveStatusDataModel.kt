package com.sooum.android.domain.model

import com.google.gson.annotations.SerializedName

data class UserActiveStatusDataModel(
    @SerializedName("banEndAt") val banEndAt: String?,
    @SerializedName("status") val status: Status
)

