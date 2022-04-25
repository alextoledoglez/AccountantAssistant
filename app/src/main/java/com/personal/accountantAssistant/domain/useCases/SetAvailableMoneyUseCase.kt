package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow

interface SetAvailableMoneyUseCase {
    operator fun invoke(value: Float): Flow<Unit>
}