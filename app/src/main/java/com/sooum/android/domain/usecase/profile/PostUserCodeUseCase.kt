package com.sooum.android.domain.usecase.profile

import com.sooum.android.domain.model.UserCodeBody
import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class PostUserCodeUseCase @Inject constructor(private val repository: MyProfileRepository) {
    suspend operator fun invoke(
        userCodeBody: UserCodeBody,
    ) = repository.postCode(userCodeBody)
}