package com.sooum.android.data.repository


import com.sooum.android.data.remote.ReportApi
import com.sooum.android.domain.repository.DetailRepository
import com.sooum.android.enums.ReportTypeEnum
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(private val reportApi: ReportApi) :
    DetailRepository {
    override suspend fun postUserReport(cardId: Long, reportTypeEnum: ReportTypeEnum) {
        reportApi.cardReport(cardId, reportTypeEnum)
    }
}