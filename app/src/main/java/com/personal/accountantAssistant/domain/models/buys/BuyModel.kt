package com.personal.accountantAssistant.domain.models.buys

import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.utils.Constants

data class BuyModel(
    var uid: Int = 0,
    var product: String? = null,
    var quantity: Int = 0,
    var price: Double = Constants.DEFAULT_VALUE,
    var totalValue: Double = Constants.DEFAULT_VALUE,
    var isActive: Boolean = false
) {


    constructor(product: String?) : this() {
        this.product = product
        quantity = Constants.DEFAULT_QUANTITY_VALUE
        price = Constants.DEFAULT_VALUE
        totalValue = Constants.DEFAULT_VALUE
        isActive = Constants.DEFAULT_ACTIVE_STATUS
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