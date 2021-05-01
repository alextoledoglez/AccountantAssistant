package com.personal.accountantAssistant.ui.bills.entities

import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.utils.Constants
import java.util.*

class Bills {
    var uid = 0
    var bill: String? = null
    var quantity = 0
    var date: Date? = null
    var value = 0.0
    var totalValue = 0.0
        private set
    var isActive = false

    constructor(bill: String?) {
        this.bill = bill
        quantity = Constants.DEFAULT_QUANTITY_VALUE
        date = Constants.DEFAULT_DATE_VALUE
        value = Constants.DEFAULT_VALUE
        totalValue = Constants.DEFAULT_VALUE
        isActive = Constants.DEFAULT_ACTIVE_STATUS
    }

    constructor(id: Int,
                bill: String?,
                quantity: Int,
                date: Date?,
                value: Double,
                active: Boolean) {
        uid = id
        this.bill = bill
        this.quantity = quantity
        this.date = date
        this.value = value
        isActive = active
    }

    constructor(payments: Payments) {
        uid = payments.id
        bill = payments.name
        quantity = payments.quantity
        date = payments.date
        value = payments.unitaryValue
        totalValue = payments.totalValue
        isActive = payments.isActive
    }
}