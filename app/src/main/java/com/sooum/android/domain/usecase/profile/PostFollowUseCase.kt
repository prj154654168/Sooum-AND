package com.sooum.android.domain.usecase.profile

import com.sooum.android.domain.model.FollowerBody
import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class PostFollowUseCase @Inject constructor(private val repository: MyProfileRepository) {
    suspend operator fun invoke(
        followerBody: FollowerBody
    ) = repository.postFollower(followerBody)
}