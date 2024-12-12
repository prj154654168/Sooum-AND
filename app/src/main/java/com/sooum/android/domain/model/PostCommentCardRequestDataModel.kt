package com.sooum.android.domain.model

import com.sooum.android.enums.FontEnum
import com.sooum.android.enums.ImgTypeEnum

data class PostCommentCardRequestDataModel (
    val isDistanceShared: Boolean,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val content: String,
    val font: FontEnum,
    val imgType: ImgTypeEnum,
    val imgName: String,
    val commentTags: List<String>? = null
)