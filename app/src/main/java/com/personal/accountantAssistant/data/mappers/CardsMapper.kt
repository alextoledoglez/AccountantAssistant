package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.CardEntity
import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*

fun CardEntity.toSummaryModel() = SummaryModel(
    activeCount = active.orZero(),
    total = availableValue?.toBigDecimal()?.rounded().orZero()
)

fun CardEntity.toModel() = CardModel(
    id = id?.toLong().orZero(),
    company = company.orEmpty(),
    name = name.orEmpty(),
    date = date.toDate(),
    password = password.orEmpty(),
    availableValue = availableValue?.toBigDecimal()?.rounded().orZero(),
    usedValue = usedValue?.toBigDecimal()?.rounded().orZero(),
    limitValue = limitValue?.toBigDecimal()?.rounded().orZero(),
    isActive = active.isMoreThanZero()
)

fun CardEntity.toExpenseEntity() = ExpenseEntity(
    id = id,
    name = "$name ( $company )",
    quantity = Int.ONE,
    date = date,
    unitaryValue = availableValue,
    totalValue = limitValue,
    type = ExpensesType.BILL.name,
    active = Int.ONE
)

fun CardModel.toExpenseEntity() = ExpenseEntity(
    id = id.toEntityId(),
    name = "$name ( $company )",
    quantity = Int.ONE,
    date = date.toDateStr(),
    unitaryValue = availableValue.toDouble(),
    totalValue = limitValue.toDouble(),
    type = ExpensesType.BILL.name,
    active = Int.ONE
)

fun CardModel.toEntity() = CardEntity(
    id = id.toEntityId(),
    company = company,
    name = name,
    date = date.toDateStr(),
    password = password,
    availableValue = availableValue.toDouble(),
    usedValue = usedValue.toDouble(),
    limitValue = limitValue.toDouble(),
    active = isActive.toInt()
)

fun List<CardEntity>.toListModel() = map { it.toModel() }.toMutableList()