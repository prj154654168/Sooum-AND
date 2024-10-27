package com.sooum.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.usecase.detail.ReportUserUseCase
import com.sooum.android.enums.ReportTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val postReportUserUseCase: ReportUserUseCase) : ViewModel()  {
    fun reportUser(cardId: Long, reportTypeEnum: ReportTypeEnum) {
        viewModelScope.launch {
            postReportUserUseCase(cardId, reportTypeEnum)
        }
    }
}