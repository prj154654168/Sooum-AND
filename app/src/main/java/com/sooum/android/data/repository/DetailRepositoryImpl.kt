package com.sooum.android.data.repository


import com.sooum.android.data.remote.CardApi
import com.sooum.android.data.remote.ReportApi
import com.sooum.android.domain.model.PostCommentCardRequestDataModel
import com.sooum.android.domain.model.Status
import com.sooum.android.domain.repository.DetailRepository
import com.sooum.android.enums.ReportTypeEnum
import javax.inject.Inject

class DetailRepositoryImpl @Inject constructor(private val reportApi: ReportApi, private val cardApi: CardApi) :
    DetailRepository {
    override suspend fun postUserReport(cardId: Long, reportTypeEnum: ReportTypeEnum) {
        reportApi.cardReport(cardId, reportTypeEnum)
    }

    override suspend fun postCommentCard(
        cardId: Long,
        postCommentCardRequest: PostCommentCardRequestDataModel
    ): Status {
        val response = cardApi.postCommentCard(cardId, postCommentCardRequest)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("No body found") // 바디가 null인 경우 예외 처리
        } else {
            // 실패한 경우의 에러 메시지를 로그로 출력
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            throw Exception("Failed to get default image: $errorMessage")
        }
    }
}