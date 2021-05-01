package com.personal.accountantAssistant.ui.payments.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.ui.bills.entities.Bills
import com.personal.accountantAssistant.ui.buys.entities.Buys
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import java.io.Serializable
import java.util.*

@Entity
class Payments : Serializable {
    @PrimaryKey
    var id = 0

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "quantity")
    var quantity = 0

    @ColumnInfo(name = "date")
    var date: Date? = null

    @ColumnInfo(name = "unitary_value")
    var unitaryValue = 0.0

    @ColumnInfo(name = "total_value")
    var totalValue = 0.0

    @ColumnInfo(name = "type")
    var type: PaymentsType? = null

    @ColumnInfo(name = "active")
    var isActive = false

    constructor()
    constructor(id: Int,
                name: String?,
                quantity: Int,
                date: Date?,
                unitaryValue: Double,
                type: PaymentsType?,
                active: Boolean) {
        this.id = id
        this.name = name
        this.quantity = quantity
        this.date = date
        this.unitaryValue = unitaryValue
        totalValue = getTotalValue()
        this.type = type
        isActive = active
    }

    constructor(bill: Bills) {
        id = bill.uid
        name = bill.bill
        quantity = bill.quantity
        date = bill.date
        unitaryValue = bill.value
        totalValue = getTotalValue()
        type = PaymentsType.BILL
        isActive = bill.isActive
    }

    constructor(buy: Buys) {
        id = buy.uid
        name = buy.product
        quantity = buy.quantity
        date = Date()
        unitaryValue = buy.price
        totalValue = getTotalValue()
        type = PaymentsType.BUY
        isActive = buy.isActive
    }

    @JvmName("getTotalValue1")
    fun getTotalValue(): Double {
        totalValue = unitaryValue * quantity
        return totalValue
    }

    @JvmName("setTotalValue1")
    fun setTotalValue(totalValue: Double) {
        this.totalValue = totalValue
    }

    val isBill: Boolean?
        get() = type?.let { PaymentsType.isBill(it) }
    val isBuy: Boolean?
        get() = type?.let { PaymentsType.isBuy(it) }

    fun equalsTo(payment: Payments): Boolean {
        return type == payment.type &&
                name == payment.name &&
                quantity == payment.quantity &&
                unitaryValue == payment.unitaryValue
    }
}