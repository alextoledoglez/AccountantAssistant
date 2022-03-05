package com.personal.accountantAssistant.domain.models.buys

import com.personal.accountantAssistant.data.entities.expenses.ExpenseEntity
import com.personal.accountantAssistant.extensions.DEFAULT_ACTIVE_STATUS
import com.personal.accountantAssistant.extensions.DEFAULT_QUANTITY_VALUE
import java.math.BigDecimal

data class BuyModel(
    var uid: Int = 0,
    var product: String? = null,
    var quantity: Int = 0,
    var price: BigDecimal = BigDecimal.ZERO,
    var totalValue: BigDecimal = BigDecimal.ZERO,
    var isActive: Boolean = false
) {


    constructor(product: String?) : this() {
        this.product = product
        quantity = Int.DEFAULT_QUANTITY_VALUE
        price = BigDecimal.ZERO
        totalValue = BigDecimal.ZERO
        isActive = Boolean.DEFAULT_ACTIVE_STATUS
    }

    constructor(
        id: Int, product: String?, quantity: Int, price: BigDecimal, active: Boolean
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