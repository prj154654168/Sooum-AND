package com.sooum.android.domain.usecase.profile

import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class DifFeedCardUseCase @Inject constructor(private val repository: MyProfileRepository) {
    suspend operator fun invoke(
        targetMemberId: Long,
    ) = repository.getDifFeedCard(targetMemberId = targetMemberId)
}