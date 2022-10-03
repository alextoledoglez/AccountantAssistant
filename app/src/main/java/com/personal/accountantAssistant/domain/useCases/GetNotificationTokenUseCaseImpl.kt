package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.repository.NotificationRepository

class GetNotificationTokenUseCaseImpl(
    private val repository: NotificationRepository
) : GetNotificationTokenUseCase {
    override fun invoke() = repository.getNotificationToken()
}