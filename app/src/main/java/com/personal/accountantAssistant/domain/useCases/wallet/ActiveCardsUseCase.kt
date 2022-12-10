package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.models.CardModel
import kotlinx.coroutines.flow.Flow

interface ActiveCardsUseCase {
    fun switchActiveCard(model: CardModel): Flow<MutableList<CardModel>?>
    fun setAllCardsActive(isActive: Boolean): Flow<MutableList<CardModel>?>
}