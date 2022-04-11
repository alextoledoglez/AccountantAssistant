package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.enums.DefaultCardsEnum
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toEntityId
import com.personal.accountantAssistant.extensions.toRoundedBigDecimal
import com.personal.accountantAssistant.utils.CalculatorUtils
import java.math.BigDecimal

fun CardEntity.toModel() = CardModel(
    id = id?.toLong().orZero(),
    company = company.orEmpty(),
    name = name.orEmpty(),
    password = password.orEmpty(),
    value = value?.toRoundedBigDecimal().orZero(),
    isActive = isActive.toBoolean().orFalse()
)

fun CardModel.toEntity() = CardEntity(
    id = id.toEntityId(),
    company = company,
    name = name,
    password = password,
    value = value.toString(),
    isActive = isActive.orFalse().toString()
)

fun MutableList<CardModel>.toSummaryModel() = SummaryModel(
    isAllChecked = isAllCardsActive(),
    isAnyChecked = isAnyCardActive(),
    total = getTotalValue()
)

fun List<CardEntity>.toListModel() = map { it.toModel() }.sortedWith(
    Comparator.comparing<CardModel?, Boolean?> { it.isActive }.thenComparingDouble {
        it.value.orZero().toDouble()
    }
).reversed().toMutableList()

fun List<CardModel>.isAllCardsActive() = stream().allMatch { it.isActive }

fun List<CardModel>.isAnyCardActive() = stream().allMatch { it.isActive }

fun List<CardModel>.getTotalValue(): BigDecimal = stream().filter { it.isActive }
    .map { it.value }.reduce(BigDecimal.ZERO, CalculatorUtils.accumulatedDecimalSum)

fun DefaultCardsEnum.toCardEntity() = CardEntity(company, title)