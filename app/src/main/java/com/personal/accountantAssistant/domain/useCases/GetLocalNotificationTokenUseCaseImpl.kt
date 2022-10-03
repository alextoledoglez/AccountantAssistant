package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.repository.NotificationRepository

class GetLocalNotificationTokenUseCaseImpl(
    private val repository: NotificationRepository
) : GetLocalNotificationTokenUseCase {
    override fun invoke() = repository.getLocalNotificationToken()
}