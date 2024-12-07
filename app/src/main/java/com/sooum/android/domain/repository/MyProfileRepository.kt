package com.sooum.android.domain.repository

import com.sooum.android.domain.model.CodeDataModel
import com.sooum.android.domain.model.MyCommentCardDataModel
import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.model.MyProfileDataModel
import com.sooum.android.domain.model.NoticeDataModel
import com.sooum.android.domain.model.UserCodeBody

interface MyProfileRepository {
    suspend fun getMyProfile(): MyProfileDataModel
    suspend fun getMyFeedCard(): List<MyFeedCardDataModel.MyFeedCardDto>

    suspend fun getMyCommentCard(): List<MyCommentCardDataModel.MyCommentCardDto>

    suspend fun getNotice(): List<NoticeDataModel.NoticeDto>
    suspend fun deleteUser()

    suspend fun getCode(): CodeDataModel
    suspend fun patchCode(): CodeDataModel

    suspend fun postCode(userCodeBody: UserCodeBody)
}