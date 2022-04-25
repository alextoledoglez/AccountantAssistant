package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow
import java.util.*

interface SetLastDateUseCase {
    operator fun invoke(value: Date?): Flow<Unit>
}