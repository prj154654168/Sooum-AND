package com.sooum.android.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.domain.usecase.detail.ReportUserUseCase
import com.sooum.android.enums.ReportTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val postReportUserUseCase: ReportUserUseCase) :
    ViewModel() {
    var httpCode: Int by mutableStateOf(0)
    fun reportUser(cardId: Long, reportTypeEnum: ReportTypeEnum) {
        viewModelScope.launch {
            try {
                httpCode = postReportUserUseCase(cardId, reportTypeEnum).code()
                Log.e("httpCode",httpCode.toString())
            } catch (E: Exception) {
                println(E)
            }
        }
    }
}