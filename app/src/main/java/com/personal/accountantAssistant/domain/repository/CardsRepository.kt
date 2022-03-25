package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.CardModel
import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    fun getCards(): Flow<MutableList<CardModel>?>
    fun saveCard(model: CardModel): Flow<Unit>
    fun setDefaultCards(): Flow<MutableList<CardModel>?>
    fun setAllCardsActive(isActive: Boolean): Flow<MutableList<CardModel>?>
    fun switchActiveCard(model: CardModel): Flow<MutableList<CardModel>?>
    fun deleteCard(model: CardModel): Flow<MutableList<CardModel>?>
    fun deleteAllCards(): Flow<MutableList<CardModel>?>
}