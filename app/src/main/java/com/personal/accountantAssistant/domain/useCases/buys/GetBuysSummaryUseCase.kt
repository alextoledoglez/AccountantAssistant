package com.personal.accountantAssistant.domain.useCases.buys

import com.personal.accountantAssistant.domain.models.SummaryModel
import kotlinx.coroutines.flow.Flow

interface GetBuysSummaryUseCase {
    operator fun invoke(): Flow<SummaryModel?>
}