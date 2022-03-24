package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.CardModel
import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    fun getCards(): Flow<MutableList<CardModel>?>
    fun saveCard(model: CardModel): Flow<Unit>
    fun setDefaultCards(): Flow<Unit>
    fun setAllCardsActive(isActive: Boolean): Flow<Unit>
    fun updateCard(model: CardModel): Flow<Unit>
    fun deleteCard(model: CardModel): Flow<Unit>
    fun deleteAllCards(): Flow<Unit>
}