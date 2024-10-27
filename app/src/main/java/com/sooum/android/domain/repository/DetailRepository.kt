package com.sooum.android.domain.repository

import com.sooum.android.enums.ReportTypeEnum

interface DetailRepository {
    suspend fun postUserReport(cardId: Long, reportTypeEnum: ReportTypeEnum)
}