package com.personal.accountantAssistant.data.remote

import com.personal.accountantAssistant.data.dao.CardDao
import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.enums.DefaultCardsEnum
import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toListModel
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.WalletModel
import com.personal.accountantAssistant.extensions.DEFAULT_UID
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.utils.CalculatorUtils
import java.math.BigDecimal
import java.util.stream.Collectors

class CardsRemoteDataSource(private val cardDao: CardDao) {

    private suspend fun getCardRecords(): List<CardModel> =
        cardDao.selectAll().toList().toListModel()

    private fun isAllActiveFrom(list: List<CardModel>) = list.stream().allMatch { it.isActive }

    private fun isAnyActiveFrom(list: List<CardModel>) = list.stream().anyMatch { it.isActive }

    private fun getTotalValuesFrom(list: List<CardModel>) = list.stream().map { it.value }
        .reduce(BigDecimal.ZERO, CalculatorUtils.accumulatedDecimalSum)

    private fun getSortedCardsFrom(list: List<CardModel>) = list.stream().sorted(
        Comparator.comparing<CardModel?, Boolean?> { it.isActive }
            .thenComparingDouble { it.value.orZero().toDouble() }
    ).collect(Collectors.toList()).asReversed()

    suspend fun getWallet() = getCardRecords().let { list ->
        WalletModel(
            isAllChecked = isAllActiveFrom(list),
            isAnyChecked = isAnyActiveFrom(list),
            total = getTotalValuesFrom(list),
            cards = getSortedCardsFrom(list)
        )
    }

    suspend fun setDefaultCards() {
        if (cardDao.clearTable() >= Int.DEFAULT_UID) {
            DefaultCardsEnum.values().forEach { cardDao.insert(CardEntity(it.company, it.title)) }
        }
    }

    suspend fun setAllCardsActive(isActive: Boolean) = cardDao.activeAll(isActive.toString())

    suspend fun updateCard(model: CardModel) = cardDao.update(model.toEntity())

    suspend fun deleteCard(model: CardModel) = cardDao.delete(model.toEntity())

    suspend fun deleteAllCards() = cardDao.clearTable()
}