package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.CardDao
import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.enums.DefaultCardsEnum
import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.data.mappers.toWalletModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.DEFAULT_UID

class CardsRemoteDataSource(private val cardDao: CardDao) {

    suspend fun getWallet() = cardDao.selectAll().toList().toListModel().toWalletModel()

    suspend fun saveCard(model: CardModel?) = model?.let {
        if (it.id > Int.DEFAULT_UID)
            cardDao.update(it.toEntity())
        else
            cardDao.insert(it.toEntity())
    }

    suspend fun setDefaultCards() {
        deleteAllCards()
        DefaultCardsEnum.values().forEach { cardDao.insert(CardEntity(it.company, it.title)) }
    }

    suspend fun setAllCardsActive(isActive: Boolean) = cardDao.activeAll(isActive.toString())

    suspend fun updateCard(model: CardModel) = cardDao.update(model.toEntity())

    suspend fun deleteCard(model: CardModel) = cardDao.delete(model.toEntity())

    suspend fun deleteAllCards() = cardDao.clearTable()
}