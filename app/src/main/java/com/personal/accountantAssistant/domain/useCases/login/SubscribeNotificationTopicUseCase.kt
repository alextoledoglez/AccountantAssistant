package com.personal.accountantAssistant.domain.useCases.login

import kotlinx.coroutines.flow.Flow

interface SubscribeNotificationTopicUseCase {
    operator fun invoke(): Flow<Void?>
}