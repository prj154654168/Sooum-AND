package com.sooum.android.data.remote


import com.sooum.android.domain.model.ReportTypeBody
import com.sooum.android.domain.model.Status
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportApi {
    @POST("/report/cards/{cardPk}")
    suspend fun cardReport(
        @Path("cardPk") cardPk: Long,
        @Body reportTypeBody: ReportTypeBody,
    ): Response<Status>
}