package com.sooum.android.domain.repository

import com.sooum.android.domain.model.PostCommentCardRequestDataModel
import com.sooum.android.domain.model.ReportTypeBody
import com.sooum.android.domain.model.Status
import com.sooum.android.enums.ReportTypeEnum
import retrofit2.Response

interface DetailRepository {
    suspend fun postUserReport(cardId: Long, reportTypeBody: ReportTypeBody): Response<Status>

    suspend fun postCommentCard(
        cardId: Long,
        postCommentCardRequest: PostCommentCardRequestDataModel,
    ): Status
}