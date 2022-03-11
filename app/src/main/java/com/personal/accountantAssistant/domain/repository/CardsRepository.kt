package com.personal.accountantAssistant.domain.repository

import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.WalletModel
import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    fun getWallet(): Flow<WalletModel>
    fun saveCard(model: CardModel): Flow<Unit>
    fun setDefaultCards(): Flow<Unit>
    fun setAllCardsActive(isActive: Boolean): Flow<Unit>
    fun updateCard(model: CardModel): Flow<Unit>
    fun deleteCard(model: CardModel): Flow<Unit>
    fun deleteAllCards(): Flow<Unit>
}