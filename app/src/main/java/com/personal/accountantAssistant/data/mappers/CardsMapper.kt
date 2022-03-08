package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.rounded
import java.math.BigDecimal

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

fun List<CardEntity>.toListModel() = map { it.toModel() }