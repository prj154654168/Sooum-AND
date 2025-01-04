package com.sooum.android.domain.usecase.notification

import com.sooum.android.domain.repository.NotificationRepository
import javax.inject.Inject

class ReadNotificationUseCase @Inject constructor(private val repository: NotificationRepository){
    suspend operator fun invoke(notificationId: Long): Int {
        return repository.patchNotificationRead(notificationId)
    }
}