package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow
import java.util.*

interface SetFirstDateUseCase {
    operator fun invoke(value: Date?): Flow<Unit>
}