package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.WalletModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toRoundedBigDecimal
import com.personal.accountantAssistant.utils.CalculatorUtils
import java.math.BigDecimal
import java.util.stream.Collectors

fun CardEntity.toModel() = CardModel(
    id = id?.toLong().orZero(),
    company = company.orEmpty(),
    name = name.orEmpty(),
    password = password?.toInt().orZero(),
    value = value?.toRoundedBigDecimal().orZero(),
    isActive = isActive.toBoolean().orFalse()
)

fun CardModel.toEntity() = CardEntity(
    id = id.toInt(),
    company = company,
    name = name,
    password = password.toString(),
    value = value.toString(),
    isActive = isActive.orFalse().toString()
)

fun ArrayList<CardModel>.toWalletModel() = WalletModel(
    isAllChecked = isAllCardsActive(),
    isAnyChecked = isAnyCardActive(),
    total = getTotalValue(),
    cards = getSortedCards().toMutableList() as ArrayList
)

fun List<CardEntity>.toListModel() = map { it.toModel() } as ArrayList

fun List<CardModel>.isAllCardsActive() = stream().allMatch { it.isActive }

fun List<CardModel>.isAnyCardActive() = stream().allMatch { it.isActive }

fun List<CardModel>.getTotalValue(): BigDecimal = stream().filter { it.isActive }
    .map { it.value }.reduce(BigDecimal.ZERO, CalculatorUtils.accumulatedDecimalSum)

fun ArrayList<CardModel>.getSortedCards() = stream().sorted(
    Comparator.comparing<CardModel?, Boolean?> {
        it.isActive
    }.thenComparingDouble { it.value.orZero().toDouble() }
).collect(Collectors.toList()).asReversed()