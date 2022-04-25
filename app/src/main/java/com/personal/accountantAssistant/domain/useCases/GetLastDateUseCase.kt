package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow
import java.util.*

interface GetLastDateUseCase {
    operator fun invoke(): Flow<Date?>
}