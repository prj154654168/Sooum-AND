package com.sooum.android.domain.usecase.user

import com.sooum.android.domain.repository.UserRepository
import javax.inject.Inject

class PostMemberSuspensionUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(encryptedDeviceId: String) = repository.postMemberSuspension(encryptedDeviceId)
}