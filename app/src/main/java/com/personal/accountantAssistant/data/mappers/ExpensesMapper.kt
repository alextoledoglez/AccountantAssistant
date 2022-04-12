package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.BillsEnum
import com.personal.accountantAssistant.data.enums.BuysEnum
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.domain.models.SummaryModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.rounded
import com.personal.accountantAssistant.extensions.toEntityId
import com.personal.accountantAssistant.utils.CalculatorUtils
import com.personal.accountantAssistant.utils.DateUtils
import java.math.BigDecimal

fun ExpenseEntity.toModel() = ExpenseModel(
    id = id?.toLong().orZero(),
    name = name,
    quantity = quantity?.toInt().orZero(),
    date = DateUtils.toDate(date.orEmpty()),
    unitaryValue = unitaryValue?.toBigDecimal()?.rounded().orZero(),
    totalValue = totalValue?.toBigDecimal()?.rounded().orZero(),
    type = ExpensesType.valueOf(type ?: ExpensesType.NONE.name),
    isActive = isActive.orFalse()
)

fun ExpenseModel.toEntity() = ExpenseEntity(
    id = id.toEntityId(),
    name = name,
    quantity = quantity,
    date = DateUtils.toString(date),
    unitaryValue = unitaryValue.toDouble(),
    totalValue = calculateTotalValue().toDouble(),
    type = type.toString(),
    isActive = isActive
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

fun ExpenseModel.toBuy() = this.copy(type = ExpensesType.BUY)

fun ExpenseModel.toBill() = this.copy(type = ExpensesType.BILL)

fun ExpenseModel.isBill() = type?.let { ExpensesType.isBill(it) }.orFalse()

fun BuysEnum.toExpenseModel() = ExpenseModel(value, ExpensesType.BILL)

fun BillsEnum.toExpenseModel() = ExpenseModel(value, ExpensesType.BILL)