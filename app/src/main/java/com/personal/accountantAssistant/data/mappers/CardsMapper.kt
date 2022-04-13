package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.enums.DefaultCardsEnum
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*

fun CardEntity.toSummaryModel() = SummaryModel(
    activeCount = active.orZero(),
    total = value?.toBigDecimal()?.rounded().orZero()
)

fun CardEntity.toModel() = CardModel(
    id = id?.toLong().orZero(),
    company = company.orEmpty(),
    name = name.orEmpty(),
    password = password.orEmpty(),
    value = value?.toBigDecimal()?.rounded().orZero(),
    isActive = active.isMoreThanZero()
)

fun CardModel.toEntity() = CardEntity(
    id = id.toEntityId(),
    company = company,
    name = name,
    password = password,
    value = value.toDouble(),
    active = isActive.toInt()
)

fun List<CardEntity>.toListModel() = map { it.toModel() }.toMutableList()

fun DefaultCardsEnum.toCardEntity() = CardEntity(company, title)