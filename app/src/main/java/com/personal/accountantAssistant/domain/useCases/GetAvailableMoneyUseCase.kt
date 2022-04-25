package com.personal.accountantAssistant.domain.useCases

import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface GetAvailableMoneyUseCase {
    operator fun invoke(): Flow<BigDecimal?>
}