package com.personal.accountantAssistant.domain.useCases

import com.personal.accountantAssistant.domain.repository.NotificationRepository

class SubscribeNotificationTopicUseCaseImpl(
    private val repository: NotificationRepository
) : SubscribeNotificationTopicUseCase {
    override fun invoke() = repository.subscribeNotificationTopic()
}