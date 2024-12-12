package com.sooum.android.domain.model

import com.sooum.android.enums.FontEnum
import com.sooum.android.enums.ImgTypeEnum

data class PostFeedRequestDataModel (
    val isDistanceShared: Boolean,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isPublic: Boolean,
    val isStory: Boolean,
    val content: String,
    val font: FontEnum,
    val imgType: ImgTypeEnum,
    val imgName: String,
    val feedTags: List<String>? = null
)