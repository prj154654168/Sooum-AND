package com.sooum.android.domain.usecase.profile

import com.sooum.android.domain.model.NicknameBody
import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class NicknameAvailableUseCase @Inject constructor(private val repository: MyProfileRepository) {
    suspend operator fun invoke(
        nicknameBody: NicknameBody,
    ) = repository.nicknameAvailable(nicknameBody)
}