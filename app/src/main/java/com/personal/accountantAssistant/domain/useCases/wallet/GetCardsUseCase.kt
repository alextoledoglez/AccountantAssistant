package com.personal.accountantAssistant.domain.useCases.wallet

import com.personal.accountantAssistant.domain.models.CardModel
import kotlinx.coroutines.flow.Flow

interface GetCardsUseCase {
    operator fun invoke(): Flow<MutableList<CardModel>?>
}