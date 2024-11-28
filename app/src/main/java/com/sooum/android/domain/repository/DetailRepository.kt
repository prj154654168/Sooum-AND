package com.sooum.android.domain.repository

import com.sooum.android.domain.model.PostCommentCardRequestDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.enums.ReportTypeEnum

interface DetailRepository {
    suspend fun postUserReport(cardId: Long, reportTypeEnum: ReportTypeEnum)

    suspend fun postCommentCard(cardId: Long, postCommentCardRequest: PostCommentCardRequestDataModel) : Status
}