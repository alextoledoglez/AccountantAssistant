package com.personal.accountantAssistant.domain.useCases.bills

import com.personal.accountantAssistant.domain.models.SummaryModel
import kotlinx.coroutines.flow.Flow

interface GetBillsSummaryUseCase {
    operator fun invoke(): Flow<SummaryModel?>
}