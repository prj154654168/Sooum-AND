package com.sooum.android.domain.repository

import com.sooum.android.domain.model.CodeDataModel
import com.sooum.android.domain.model.DeleteUserBody
import com.sooum.android.domain.model.DifProfileDataModel
import com.sooum.android.domain.model.FollowerBody
import com.sooum.android.domain.model.FollowerDataModel
import com.sooum.android.domain.model.FollowingDataModel
import com.sooum.android.domain.model.MyCommentCardDataModel
import com.sooum.android.domain.model.MyFeedCardDataModel
import com.sooum.android.domain.model.MyProfileDataModel
import com.sooum.android.domain.model.NoticeDataModel
import com.sooum.android.domain.model.UserCodeBody

interface MyProfileRepository {
    suspend fun getMyProfile(): MyProfileDataModel
    suspend fun getMyFeedCard(): List<MyFeedCardDataModel.MyFeedCardDto>

    suspend fun getDifProfile(memberId: Long): DifProfileDataModel
    suspend fun getDifFeedCard(targetMemberId: Long): List<MyFeedCardDataModel.MyFeedCardDto>

    suspend fun getMyCommentCard(): List<MyCommentCardDataModel.MyCommentCardDto>

    suspend fun getNotice(): List<NoticeDataModel.NoticeDto>
    suspend fun deleteUser(deleteUserBody: DeleteUserBody)

    suspend fun getCode(): CodeDataModel
    suspend fun patchCode(): CodeDataModel

    suspend fun postCode(userCodeBody: UserCodeBody)

    suspend fun getFollower(): FollowerDataModel

    suspend fun getDifFollower(profileOwnerPk: Long): FollowerDataModel
    suspend fun getFollowing(): FollowingDataModel

    suspend fun getDifFollowing(profileOwnerPk: Long): FollowingDataModel
    suspend fun postFollower(followerBody: FollowerBody)
    suspend fun deleteFollower(toMemberId: Long)
}
