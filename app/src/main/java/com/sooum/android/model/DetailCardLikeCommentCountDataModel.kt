package com.sooum.android.model

data class DetailCardLikeCommentCountDataModel(
    val cardLikeCnt: Int,
    val commentCnt: Int,
    val isLiked: Boolean,
    val status: Status,
)