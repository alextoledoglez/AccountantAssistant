package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.utils.DateUtils

fun ExpenseEntity.toModel() = ExpenseModel(
    id = id?.toLong().orZero(),
    name = name,
    quantity = quantity?.toInt().orZero(),
    date = DateUtils.toDate(date.orEmpty()),
    unitaryValue = unitaryValue?.toBigDecimal().orZero(),
    totalValue = totalValue?.toBigDecimal().orZero(),
    type = ExpensesType.valueOf(type ?: ExpensesType.NONE.name),
    isActive = isActive.toBoolean().orFalse()
)

fun ExpenseModel.toEntity() = ExpenseEntity(
    id = id.toInt(),
    name = name,
    quantity = quantity.toString(),
    date = DateUtils.toString(date),
    unitaryValue = unitaryValue.toString(),
    totalValue = calculateTotalValue().toString(),
    type = type.toString(),
    isActive = isActive.toString()
)

fun List<ExpenseEntity>.toListModel() = map { it.toModel() }

fun ExpenseModel.isBill() = type?.let { ExpensesType.isBill(it) }.orFalse()