package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.*

fun ExpenseEntity.toSummaryModel() = SummaryModel(
    activeCount = active.orZero(),
    total = totalValue?.toBigDecimal()?.rounded().orZero()
)

fun ExpenseEntity.toModel() = ExpenseModel(
    id = id?.toLong().orZero(),
    name = name,
    quantity = quantity.orZero(),
    date = date.toDate(),
    unitaryValue = unitaryValue?.toBigDecimal()?.rounded().orZero(),
    totalValue = totalValue?.toBigDecimal()?.rounded().orZero(),
    type = ExpensesType.valueOf(type ?: ExpensesType.NONE.name),
    isActive = active.isMoreThanZero()
)

fun ExpenseModel.toEntity() = ExpenseEntity(
    id = id.toEntityId(),
    name = name,
    quantity = quantity,
    date = date.toDateStr(),
    unitaryValue = unitaryValue.toDouble(),
    totalValue = calculateTotalValue().toDouble(),
    type = type.toString(),
    active = isActive.toInt()
)

fun ExpenseModel?.toCalendarSelectionArgs(): Array<String?> = arrayOf(
    Int.DEFAULT_CALENDAR_ID.toString(),
    this?.name.orEmpty(),
    this?.date.toCalendarMillis().toString(),
    this?.totalValue.toString()
)

fun List<ExpenseEntity>.toListModel() = map { it.toModel() }.sortedBy { it.date }.toMutableList()

fun ExpenseModel.toBuy() = this.copy(type = ExpensesType.BUY)

fun ExpenseModel.toBill() = this.copy(type = ExpensesType.BILL)

fun ExpenseModel.isBill() = type?.let { ExpensesType.isBill(it) }.orFalse()