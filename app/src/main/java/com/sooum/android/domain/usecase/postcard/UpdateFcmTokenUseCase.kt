package com.sooum.android.domain.usecase.postcard

import com.sooum.android.domain.repository.PostCardRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val repository: PostCardRepository
) {
    suspend operator fun invoke(token: String) {
        repository.updateFcmToken(token)
    }
}