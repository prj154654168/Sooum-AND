package com.sooum.android.domain.usecase.notification

import com.sooum.android.domain.repository.NotificationRepository
import javax.inject.Inject

class AllUnreadCountUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke() = repository.getAllUnreadCount()
}