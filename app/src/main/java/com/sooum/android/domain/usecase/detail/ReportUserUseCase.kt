package com.sooum.android.domain.usecase.detail

import com.sooum.android.domain.model.ReportTypeBody
import com.sooum.android.domain.repository.DetailRepository
import com.sooum.android.enums.ReportTypeEnum
import javax.inject.Inject

class ReportUserUseCase @Inject constructor(private val repository: DetailRepository) {
    suspend operator fun invoke(
        cardId: Long,
        reportTypeBody: ReportTypeBody
    ) = repository.postUserReport(cardId, reportTypeBody)
}