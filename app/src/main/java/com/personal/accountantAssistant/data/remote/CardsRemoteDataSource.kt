package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.CardDao
import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.flowEmit
import com.personal.accountantAssistant.extensions.orZero
import kotlinx.coroutines.flow.Flow

class CardsRemoteDataSource(private val cardDao: CardDao) {

    private suspend fun getCardsBy(result: Int) = if (result > Int.DEFAULT_UID)
        cardDao.selectAll().toList()
    else
        mutableListOf()

    fun getCards(): Flow<List<CardEntity>> = flowEmit { cardDao.selectAll().toList() }

    fun getSummary(): Flow<CardEntity> = flowEmit { cardDao.getSummary() }

    fun saveCard(entity: CardEntity): Flow<List<CardEntity>> = flowEmit {
        val editedCards = if (entity.id.orZero() > Int.DEFAULT_UID)
            cardDao.update(entity)
        else
            cardDao.insert(entity).toInt()
        getCardsBy(editedCards)
    }

    fun setAllCardsActive(active: Int): Flow<List<CardEntity>> = flowEmit {
        val activeCards = cardDao.activeAll(active)
        getCardsBy(activeCards)
    }

    fun switchActiveCard(entity: CardEntity): Flow<List<CardEntity>> = flowEmit {
        val activeCard = cardDao.setActive(entity.id.orZero(), entity.active.orZero())
        getCardsBy(activeCard)
    }

    fun deleteCard(entity: CardEntity): Flow<List<CardEntity>> = flowEmit {
        val deletedCard = cardDao.delete(entity)
        getCardsBy(deletedCard)
    }

    fun deleteAllCards(): Flow<List<CardEntity>> = flowEmit {
        val deletedCards = cardDao.clearTable()
        getCardsBy(deletedCards)
    }

}