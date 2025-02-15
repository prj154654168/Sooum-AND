package com.sooum.android.domain.model

data class SuspensionResponse(
    val untilBan: String,
    val isBanUser: Boolean,
    val status: Status
)