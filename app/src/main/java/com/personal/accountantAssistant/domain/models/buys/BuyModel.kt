package com.personal.accountantAssistant.domain.models.buys

import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.extensions.DEFAULT_ACTIVE_STATUS
import com.personal.accountantAssistant.extensions.DEFAULT_QUANTITY_VALUE
import com.personal.accountantAssistant.extensions.DEFAULT_VALUE

data class BuyModel(
    var uid: Int = 0,
    var product: String? = null,
    var quantity: Int = 0,
    var price: Double = Double.DEFAULT_VALUE,
    var totalValue: Double = Double.DEFAULT_VALUE,
    var isActive: Boolean = false
) {


    constructor(product: String?) : this() {
        this.product = product
        quantity = Int.DEFAULT_QUANTITY_VALUE
        price = Double.DEFAULT_VALUE
        totalValue = Double.DEFAULT_VALUE
        isActive = Boolean.DEFAULT_ACTIVE_STATUS
    }

    constructor(
        id: Int, product: String?, quantity: Int, price: Double, active: Boolean
    ) : this() {
        uid = id
        this.product = product
        this.quantity = quantity
        this.price = price
        isActive = active
    }

    constructor(expenseEntity: ExpenseEntity) : this() {
        uid = expenseEntity.id
        product = expenseEntity.name
        quantity = expenseEntity.quantity
        price = expenseEntity.unitaryValue
        totalValue = expenseEntity.totalValue
        isActive = expenseEntity.isActive
    }
}