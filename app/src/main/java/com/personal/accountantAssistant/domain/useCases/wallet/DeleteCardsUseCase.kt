package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.models.CardModel
import kotlinx.coroutines.flow.Flow

interface DeleteCardsUseCase {
    fun deleteCard(model: CardModel): Flow<MutableList<CardModel>?>
    fun deleteAllCards(): Flow<MutableList<CardModel>?>
}