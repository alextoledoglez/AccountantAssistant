package com.personal.accountantAssistant.domain.useCases.login

import kotlinx.coroutines.flow.Flow

interface SetLocalNotificationTokenUseCase {
    operator fun invoke(token: String?): Flow<Boolean?>
}