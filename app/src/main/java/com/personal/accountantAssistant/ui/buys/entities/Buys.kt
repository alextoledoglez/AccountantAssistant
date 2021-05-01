package com.personal.accountantAssistant.ui.buys.entities

import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.utils.Constants

class Buys {
    var uid = 0
    var product: String? = null
    var quantity = 0
    var price = 0.0
    var totalValue = 0.0
        private set
    var isActive = false

    constructor(product: String?) {
        this.product = product
        quantity = Constants.DEFAULT_QUANTITY_VALUE
        price = Constants.DEFAULT_VALUE
        totalValue = Constants.DEFAULT_VALUE
        isActive = Constants.DEFAULT_ACTIVE_STATUS
    }

    constructor(id: Int,
                product: String?,
                quantity: Int,
                price: Double,
                active: Boolean) {
        uid = id
        this.product = product
        this.quantity = quantity
        this.price = price
        isActive = active
    }

    constructor(payments: Payments) {
        uid = payments.id
        product = payments.name
        quantity = payments.quantity
        price = payments.unitaryValue
        totalValue = payments.totalValue
        isActive = payments.isActive
    }
}