package com.sooum.android.domain.usecase.profile

import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class MyFeedCardUseCase @Inject constructor(private val repository: MyProfileRepository) {
    suspend operator fun invoke(
    ) = repository.getMyFeedCard()
}