package com.sooum.android.data.remote

import com.sooum.android.enums.ReportTypeEnum
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReportApi {
    @POST("/report/cards/{cardPk}")
    suspend fun cardReport(

        @Path("cardPk") cardPk: Long,
        @Query("reportType") reportTypeEnum : ReportTypeEnum
    )
}