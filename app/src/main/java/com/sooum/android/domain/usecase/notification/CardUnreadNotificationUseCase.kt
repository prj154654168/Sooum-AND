package com.sooum.android.domain.usecase.notification

import com.sooum.android.domain.repository.NotificationRepository
import javax.inject.Inject

class CardUnreadNotificationUseCase @Inject constructor(private val repository: NotificationRepository){
    operator fun invoke() =  repository.getCardUnreadNotification()
}