package com.sooum.android.domain.usecase.profile

import com.sooum.android.domain.model.NotifyBody
import com.sooum.android.domain.repository.MyProfileRepository
import javax.inject.Inject

class UpdateNotifyUseCase @Inject constructor(private val repository: MyProfileRepository) {
    suspend operator fun invoke(
        notifyBody: NotifyBody,
    ) = repository.updateNotify(notifyBody)
}