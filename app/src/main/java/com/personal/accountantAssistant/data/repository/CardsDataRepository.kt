package com.personal.accountantAssistant.data.repository

import com.personal.accountantAssistant.data.remote.CardsRemoteDataSource
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.WalletModel
import com.personal.accountantAssistant.domain.repository.CardsRepository
import com.personal.accountantAssistant.extensions.flowEmit
import kotlinx.coroutines.flow.Flow

class CardsDataRepository(
    private val dataSource: CardsRemoteDataSource
) : CardsRepository {

    override fun getWallet(): Flow<WalletModel> = flowEmit { dataSource.getWallet() }

    override fun setDefaultCards(): Flow<Unit> = flowEmit { dataSource.setDefaultCards() }

    override fun setAllCardsActive(isActive: Boolean): Flow<Unit> = flowEmit {
        dataSource.setAllCardsActive(isActive)
    }

    override fun updateCard(model: CardModel): Flow<Unit> = flowEmit {
        dataSource.updateCard(model)
    }

    override fun deleteCard(model: CardModel): Flow<Unit> = flowEmit {
        dataSource.deleteCard(model)
    }

    override fun deleteAllCards(): Flow<Unit> = flowEmit { dataSource.deleteAllCards() }

}