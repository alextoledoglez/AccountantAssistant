package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow
import java.util.*

interface GetPeriodDatesUseCase {
    operator fun invoke(): Flow<Pair<Date?, Date?>>
}