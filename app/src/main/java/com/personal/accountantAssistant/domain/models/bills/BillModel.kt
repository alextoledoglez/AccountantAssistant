package com.personal.accountantAssistant.domain.models.bills

import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.extensions.DEFAULT_ACTIVE_STATUS
import com.personal.accountantAssistant.extensions.DEFAULT_QUANTITY_VALUE
import java.math.BigDecimal
import java.util.*

data class BillModel(
    var uid: Int = 0,
    var bill: String? = null,
    var quantity: Int = 0,
    var date: Date? = null,
    var value: BigDecimal = BigDecimal.ZERO,
    var totalValue: BigDecimal = BigDecimal.ZERO,
    var isActive: Boolean = false
) {

    constructor(bill: String?) : this() {
        this.bill = bill
        quantity = Int.DEFAULT_QUANTITY_VALUE
        date = Date()
        value = BigDecimal.ZERO
        totalValue = BigDecimal.ZERO
        isActive = Boolean.DEFAULT_ACTIVE_STATUS
    }

    constructor(
        id: Int, bill: String?, quantity: Int, date: Date?, value: BigDecimal, active: Boolean
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