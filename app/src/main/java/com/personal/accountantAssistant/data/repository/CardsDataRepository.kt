package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.data.mappers.toSummaryModel
import com.personal.accountantAssistant.data.remote.CardsRemoteDataSource
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.extensions.toInt
import kotlinx.coroutines.flow.map

class CardsDataRepository(private val dataSource: CardsRemoteDataSource) : CardsRepository {

    override fun getCards() = dataSource.getCards().map { it.toListModel() }

    override fun getSummary() = dataSource.getSummary().map { it.toSummaryModel() }

    override fun saveCard(model: CardModel) = dataSource.saveCard(model.toEntity()).map {
        it.toListModel()
    }

    override fun setAllCardsActive(isActive: Boolean) = dataSource.setAllCardsActive(
        isActive.toInt()
    ).map { it.toListModel() }

    override fun switchActiveCard(model: CardModel) = dataSource.switchActiveCard(
        model.toEntity()
    ).map { it.toListModel() }

    override fun deleteCard(model: CardModel) = dataSource.deleteCard(model.toEntity()).map {
        it.toListModel()
    }

    override fun deleteAllCards() = dataSource.deleteAllCards().map { it.toListModel() }

}