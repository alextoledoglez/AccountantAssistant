package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.wallet.CardEntity
import com.personal.accountantAssistant.domain.models.wallet.CardModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero

fun CardEntity.toModel() = CardModel(
    company = company.orEmpty(),
    name = name.orEmpty(),
    password = password.orZero(),
    value = value.orZero(),
    isEnabled = isActive.orFalse()
)