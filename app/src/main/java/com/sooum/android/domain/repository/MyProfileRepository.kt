package com.sooum.android.domain.repository

import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.model.MyProfileDataModel

interface MyProfileRepository {
    suspend fun getMyProfile(): MyProfileDataModel
    suspend fun getMyFeedCard(): List<MyFeedCardDataModel.MyFeedCardDto>
}