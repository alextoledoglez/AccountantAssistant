package com.personal.accountantAssistant.domain.useCases.login

import com.personal.accountantAssistant.domain.repository.NotificationRepository

class SetLocalNotificationTokenUseCaseImpl(
    private val repository: NotificationRepository
) : SetLocalNotificationTokenUseCase {
    override fun invoke(token: String?) = repository.setLocalNotificationToken(token)
}