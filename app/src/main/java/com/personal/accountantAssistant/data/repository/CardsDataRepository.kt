package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.remote.CardsRemoteDataSource
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.extensions.flowEmit
import kotlinx.coroutines.flow.Flow

class CardsDataRepository(
    private val dataSource: CardsRemoteDataSource
) : CardsRepository {

    override fun getCards(): Flow<MutableList<CardModel>?> = flowEmit { dataSource.getCards() }

    override fun saveCard(model: CardModel): Flow<MutableList<CardModel>?> = flowEmit {
        dataSource.saveCard(model)
    }

    override fun setDefaultCards(): Flow<MutableList<CardModel>?> = flowEmit {
        dataSource.setDefaultCards()
    }

    override fun setAllCardsActive(isActive: Boolean): Flow<MutableList<CardModel>?> = flowEmit {
        dataSource.setAllCardsActive(isActive)
    }

    override fun switchActiveCard(model: CardModel): Flow<MutableList<CardModel>?> = flowEmit {
        dataSource.switchActiveCard(model)
    }

    override fun deleteCard(model: CardModel): Flow<MutableList<CardModel>?> = flowEmit {
        dataSource.deleteCard(model)
    }

    override fun deleteAllCards(): Flow<MutableList<CardModel>?> = flowEmit {
        dataSource.deleteAllCards()
    }

}