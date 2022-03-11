package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.WalletModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.rounded
import com.personal.accountantAssistant.utils.CalculatorUtils
import java.math.BigDecimal
import java.util.stream.Collectors

fun CardEntity.toModel() = CardModel(
    id = id?.toLong().orZero(),
    company = company.orEmpty(),
    name = name.orEmpty(),
    password = password?.toInt().orZero(),
    value = BigDecimal(value).rounded(),
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

fun List<CardModel>.toWalletModel() = WalletModel(
    isAllChecked = isAllCardsActive(),
    isAnyChecked = isAnyCardActive(),
    total = getTotalValue(),
    cards = getSortedCards()
)

fun List<CardEntity>.toListModel() = map { it.toModel() }

fun List<CardModel>.isAllCardsActive() = stream().allMatch { it.isActive }

fun List<CardModel>.isAnyCardActive() = stream().allMatch { it.isActive }

fun List<CardModel>.getTotalValue(): BigDecimal = stream().filter { it.isActive }
    .map { it.value }.reduce(BigDecimal.ZERO, CalculatorUtils.accumulatedDecimalSum)

fun List<CardModel>.getSortedCards() = stream().sorted(
    Comparator.comparing<CardModel?, Boolean?> {
        it.isActive
    }.thenComparingDouble { it.value.orZero().toDouble() }
).collect(Collectors.toList()).asReversed()