package com.sooum.android.domain.usecase.profile

import com.sooum.android.domain.model.EncryptedDeviceId
import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class SuspensionUseCase @Inject constructor(private val repository: MyProfileRepository) {
    suspend operator fun invoke(
        encryptedDeviceId: EncryptedDeviceId,
    ) = repository.suspension(encryptedDeviceId)
}