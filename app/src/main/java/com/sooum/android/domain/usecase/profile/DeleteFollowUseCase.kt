package com.sooum.android.domain.usecase.profile

import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class DeleteFollowUseCase @Inject constructor(private val repository: MyProfileRepository) {
    suspend operator fun invoke(
        toMemberId: Long,
    ) = repository.deleteFollower(toMemberId)
}