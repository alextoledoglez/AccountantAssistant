package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.models.SummaryModel
import kotlinx.coroutines.flow.Flow

interface GetCardsSummaryUseCase {
    operator fun invoke(): Flow<SummaryModel?>
}