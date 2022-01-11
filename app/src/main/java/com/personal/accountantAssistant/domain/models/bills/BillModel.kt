package com.personal.accountantAssistant.domain.models.bills

import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.utils.Constants
import java.util.*

data class BillModel(
    var uid: Int = 0,
    var bill: String? = null,
    var quantity: Int = 0,
    var date: Date? = null,
    var value: Double = Constants.DEFAULT_VALUE,
    var totalValue: Double = Constants.DEFAULT_VALUE,
    var isActive: Boolean = false
) {

    constructor(bill: String?) : this() {
        this.bill = bill
        quantity = Constants.DEFAULT_QUANTITY_VALUE
        date = Constants.DEFAULT_DATE_VALUE
        value = Constants.DEFAULT_VALUE
        totalValue = Constants.DEFAULT_VALUE
        isActive = Constants.DEFAULT_ACTIVE_STATUS
    }

    constructor(
        id: Int, bill: String?, quantity: Int, date: Date?, value: Double, active: Boolean
    ) : this() {
        uid = id
        this.bill = bill
        this.quantity = quantity
        this.date = date
        this.value = value
        isActive = active
    }

    constructor(expenseEntity: ExpenseEntity) : this() {
        uid = expenseEntity.id
        bill = expenseEntity.name
        quantity = expenseEntity.quantity
        date = expenseEntity.date
        value = expenseEntity.unitaryValue
        totalValue = expenseEntity.totalValue
        isActive = expenseEntity.isActive
    }
}