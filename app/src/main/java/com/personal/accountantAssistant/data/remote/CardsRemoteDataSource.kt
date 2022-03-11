package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.CardDao
import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.enums.DefaultCardsEnum
import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.data.mappers.toWalletModel
import com.personal.accountantAssistant.domain.models.CardModel

class CardsRemoteDataSource(private val cardDao: CardDao) {

    suspend fun getWallet() = cardDao.selectAll().toList().toListModel().toWalletModel()

    suspend fun setDefaultCards() {
        deleteAllCards()
        DefaultCardsEnum.values().forEach { cardDao.insert(CardEntity(it.company, it.title)) }
    }

    suspend fun setAllCardsActive(isActive: Boolean) = cardDao.activeAll(isActive.toString())

    suspend fun updateCard(model: CardModel) = cardDao.update(model.toEntity())

    suspend fun deleteCard(model: CardModel) = cardDao.delete(model.toEntity())

    suspend fun deleteAllCards() = cardDao.clearTable()
}