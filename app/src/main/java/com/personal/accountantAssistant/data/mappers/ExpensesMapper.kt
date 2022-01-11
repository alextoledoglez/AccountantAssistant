package com.personal.accountantAssistant.data.mappers

import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType

fun ExpenseEntity.toBuys() = also { it.type = ExpensesType.BUY }

fun ExpenseEntity.toBills() = also { it.type = ExpensesType.BILL }