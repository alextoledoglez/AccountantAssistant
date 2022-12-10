package com.personal.accountantAssistant.domain.useCases.login

import kotlinx.coroutines.flow.Flow

interface GetNotificationTokenUseCase {
    operator fun invoke(): Flow<String?>
}