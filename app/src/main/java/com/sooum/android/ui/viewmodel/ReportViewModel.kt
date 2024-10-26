package com.sooum.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sooum.android.data.remote.CardApi
import com.sooum.android.Constants
import com.sooum.android.data.remote.RetrofitInterface
import com.sooum.android.enums.ReportTypeEnum
import kotlinx.coroutines.launch

class ReportViewModel: ViewModel()  {

    val retrofitInstance: CardApi = RetrofitInterface.getInstance().create(CardApi::class.java)

    fun cardReport(reportTypeEnum: ReportTypeEnum, cardId: Long) {
        viewModelScope.launch {
            retrofitInstance.cardReport(Constants.ACCESS_TOKEN, cardId, reportTypeEnum)
        }
    }
}