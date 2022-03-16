package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.ExpenseEntity
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.domain.models.BillsModel
import com.personal.accountantAssistant.domain.models.BuysModel
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toRoundedBigDecimal
import com.personal.accountantAssistant.utils.CalculatorUtils
import com.personal.accountantAssistant.utils.DateUtils
import java.math.BigDecimal
import java.util.stream.Collectors

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
    id = id.toInt(),
    name = name,
    quantity = quantity.toString(),
    date = DateUtils.toString(date),
    unitaryValue = unitaryValue.toString(),
    totalValue = calculateTotalValue().toString(),
    type = type.toString(),
    isActive = isActive.toString()
)

fun ArrayList<ExpenseModel>.toBuysModel() = BuysModel(
    isAllChecked = isAllExpensesActive(),
    isAnyChecked = isAnyExpenseActive(),
    total = getTotalValue(),
    expenses = getSortedExpenses().toMutableList() as ArrayList
)

fun ArrayList<ExpenseModel>.toBillsModel() = BillsModel(
    isAllChecked = isAllExpensesActive(),
    isAnyChecked = isAnyExpenseActive(),
    total = getTotalValue(),
    expenses = getSortedExpenses() as ArrayList
)

fun List<ExpenseEntity>.toListModel() = map { it.toModel() } as ArrayList

fun List<ExpenseModel>.isAllExpensesActive() = stream().allMatch { it.isActive }

fun List<ExpenseModel>.isAnyExpenseActive() = stream().anyMatch { it.isActive }

fun List<ExpenseModel>.getTotalValue(): BigDecimal = stream().filter { it.isActive }
    .map { it.totalValue }.reduce(BigDecimal.ZERO, CalculatorUtils.accumulatedDecimalSum)

fun ArrayList<ExpenseModel>.getSortedExpenses() = stream().sorted(
    Comparator.comparing(ExpenseModel::date)
).collect(Collectors.toList()).toMutableList()

fun ExpenseModel.toBuy() = apply {
    type = ExpensesType.BUY
}

fun ExpenseModel.toBill() = apply {
    type = ExpensesType.BILL
}

fun ExpenseModel.isBill() = type?.let { ExpensesType.isBill(it) }.orFalse()