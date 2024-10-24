package com.sooum.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.CardApi
import com.sooum.android.Constants
import com.sooum.android.RetrofitInterface
import com.sooum.android.enums.ReportType
import kotlinx.coroutines.launch

class ReportViewModel: ViewModel()  {

    val retrofitInstance = RetrofitInterface.getInstance().create(CardApi::class.java)

    fun cardReport(reportType: ReportType, cardId: Long) {
        viewModelScope.launch {
            retrofitInstance.cardReport(Constants.ACCESS_TOKEN, cardId, reportType)
        }
    }
}