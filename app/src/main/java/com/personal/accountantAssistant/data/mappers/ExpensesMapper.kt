package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toEntityId
import com.personal.accountantAssistant.extensions.toRoundedBigDecimal
import com.personal.accountantAssistant.utils.CalculatorUtils
import com.personal.accountantAssistant.utils.DateUtils
import java.math.BigDecimal

fun ExpenseEntity.toModel() = ExpenseModel(
    id = id?.toLong().orZero(),
    name = name,
    quantity = quantity?.toInt().orZero(),
    date = DateUtils.toDate(date.orEmpty()),
    unitaryValue = unitaryValue?.toRoundedBigDecimal().orZero(),
    totalValue = totalValue?.toRoundedBigDecimal().orZero(),
    type = ExpensesType.valueOf(type ?: ExpensesType.NONE.name),
    isActive = isActive.toBoolean().orFalse()
)

fun ExpenseModel.toEntity() = ExpenseEntity(
    id = id.toEntityId(),
    name = name,
    quantity = quantity.toString(),
    date = DateUtils.toString(date),
    unitaryValue = unitaryValue.toString(),
    totalValue = calculateTotalValue().toString(),
    type = type.toString(),
    isActive = isActive.toString()
)

fun MutableList<ExpenseModel>.toSummaryModel() = SummaryModel(
    isAllChecked = isAllExpensesActive(),
    isAnyChecked = isAnyExpenseActive(),
    total = getTotalValue()
)

fun List<ExpenseEntity>.toListModel() = map { it.toModel() }.sortedBy { it.date }.toMutableList()

fun List<ExpenseModel>.isAllExpensesActive() = stream().allMatch { it.isActive }

fun List<ExpenseModel>.isAnyExpenseActive() = stream().anyMatch { it.isActive }

fun List<ExpenseModel>.getTotalValue(): BigDecimal = stream().filter { it.isActive }
    .map { it.totalValue }.reduce(BigDecimal.ZERO, CalculatorUtils.accumulatedDecimalSum)

fun ExpenseModel.toBuy() = apply {
    type = ExpensesType.BUY
}

fun ExpenseModel.toBill() = apply {
    type = ExpensesType.BILL
}

fun ExpenseModel.isBill() = type?.let { ExpensesType.isBill(it) }.orFalse()