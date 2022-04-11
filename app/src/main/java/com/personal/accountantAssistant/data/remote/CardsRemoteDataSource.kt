package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.CardDao
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
        return getCardsBy(edited.toInt())
    }

    suspend fun setDefaultCards(): MutableList<CardModel> {
        deleteAllCards()
        return getCardsBy(cardDao.insert(DefaultCardsEnum.toCardsEntities()).size)
    }

    suspend fun setAllCardsActive(isActive: Boolean) = getCardsBy(
        cardDao.activeAll(isActive.toString())
    )

    suspend fun switchActiveCard(model: CardModel): MutableList<CardModel> {
        val entity = model.copy(isActive = !model.isActive).toEntity()
        return getCardsBy(cardDao.update(entity))
    }

    suspend fun deleteCard(model: CardModel) = getCardsBy(cardDao.delete(model.toEntity()))

    suspend fun deleteAllCards() = getCardsBy(cardDao.clearTable())

    private suspend fun getCardsBy(result: Int) = if (result > Int.DEFAULT_UID)
        getCards()
    else
        mutableListOf()
}