package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.domain.models.bills.BillModel
import com.personal.accountantAssistant.domain.models.buys.BuyModel
import com.personal.accountantAssistant.domain.models.expenses.ExpenseModel

fun ExpenseEntity.toBuys() = also { it.type = ExpensesType.BUY }

fun ExpenseEntity.toBills() = also { it.type = ExpensesType.BILL }

fun ExpenseEntity.toModel() = ExpenseModel(
    id = id,
    name = name,
    quantity = quantity,
    date = date,
    unitaryValue = unitaryValue,
    totalValue = totalValue,
    type = type,
    isActive = isActive
)

fun ExpenseModel.toBuyModel() = BuyModel(
    uid = id,
    product = name,
    quantity = quantity,
    price = unitaryValue,
    totalValue = totalValue,
    isActive = isActive
)

fun ExpenseModel.toBillModel() = BillModel(
    uid = id,
    bill = name,
    quantity = quantity,
    date = date,
    value = unitaryValue,
    totalValue = totalValue,
    isActive = isActive
)