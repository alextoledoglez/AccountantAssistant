package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.CardDao
import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.enums.DefaultCardsEnum
import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.DEFAULT_UID

class CardsRemoteDataSource(private val cardDao: CardDao) {

    suspend fun getCards() = cardDao.selectAll().toList().toListModel()

    suspend fun saveCard(model: CardModel): MutableList<CardModel> {
        val edited = if (model.id > Int.DEFAULT_UID)
            cardDao.update(model.toEntity())
        else
            cardDao.insert(model.toEntity())
        return if (edited.toLong() > Int.DEFAULT_UID) getCards() else mutableListOf()
    }

    suspend fun setDefaultCards(): MutableList<CardModel> {
        deleteAllCards()
        val list = DefaultCardsEnum.values().map { CardEntity(it.company, it.title) }
        return if (cardDao.insert(list).size > Int.DEFAULT_UID) list.toListModel() else mutableListOf()
    }

    suspend fun setAllCardsActive(isActive: Boolean) = if (
        cardDao.activeAll(isActive.toString()) > Int.DEFAULT_UID
    ) getCards() else mutableListOf()

    suspend fun switchActiveCard(model: CardModel) = if (
        cardDao.update(model.toEntity()) > Int.DEFAULT_UID
    ) getCards() else mutableListOf()

    suspend fun deleteCard(model: CardModel) = if (
        cardDao.delete(model.toEntity()) > Int.DEFAULT_UID
    ) getCards() else mutableListOf()

    suspend fun deleteAllCards() = if (
        cardDao.clearTable() > Int.DEFAULT_UID
    ) getCards() else mutableListOf()
}