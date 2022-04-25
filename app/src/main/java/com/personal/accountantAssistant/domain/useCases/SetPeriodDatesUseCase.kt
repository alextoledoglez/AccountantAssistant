package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow
import java.util.*

interface SetPeriodDatesUseCase {
    operator fun invoke(first: Date?, last: Date?): Flow<Unit>
}