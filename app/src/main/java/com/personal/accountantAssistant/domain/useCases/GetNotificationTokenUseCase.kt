package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow

interface GetNotificationTokenUseCase {
    operator fun invoke(): Flow<String?>
}