package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow

interface GetLocalNotificationTokenUseCase {
    operator fun invoke(): Flow<String?>
}