package com.sooum.android.data.remote


import com.sooum.android.domain.model.Status
import com.sooum.android.enums.ReportTypeEnum
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportApi {
    @POST("/report/cards/{cardPk}")
    suspend fun cardReport(
        @Path("cardPk") cardPk: Long,
        @Body reportTypeEnum: ReportTypeEnum,
    ): Response<Status>
}