package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.models.CardModel
import kotlinx.coroutines.flow.Flow

interface SaveCardUseCase {
    operator fun invoke(model: CardModel): Flow<MutableList<CardModel>?>
}