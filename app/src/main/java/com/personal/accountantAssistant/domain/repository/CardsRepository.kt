package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    fun getCards(): Flow<MutableList<CardModel>?>
    fun getSummary(): Flow<SummaryModel?>
    fun saveCard(model: CardModel): Flow<MutableList<CardModel>?>
    fun setAllCardsActive(isActive: Boolean): Flow<MutableList<CardModel>?>
    fun switchActiveCard(model: CardModel): Flow<MutableList<CardModel>?>
    fun deleteCard(model: CardModel): Flow<MutableList<CardModel>?>
    fun deleteAllCards(): Flow<MutableList<CardModel>?>
}