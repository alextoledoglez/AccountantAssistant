package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow

interface SubscribeNotificationTopicUseCase {
    operator fun invoke(): Flow<Void?>
}