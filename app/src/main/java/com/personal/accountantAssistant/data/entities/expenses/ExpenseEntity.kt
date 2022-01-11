package com.personal.accountantAssistant.data.entities.expenses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.domain.models.bills.BillModel
import com.personal.accountantAssistant.domain.models.buys.BuyModel
import java.io.Serializable
import java.util.*

@Entity
class ExpenseEntity : Serializable {
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
    var type: ExpensesType? = null

    @ColumnInfo(name = "active")
    var isActive = false

    constructor()

    constructor(
        id: Int,
        name: String?,
        quantity: Int,
        date: Date?,
        unitaryValue: Double,
        type: ExpensesType?,
        active: Boolean
    ) {
        this.id = id
        this.name = name
        this.quantity = quantity
        this.date = date
        this.unitaryValue = unitaryValue
        totalValue = getTotalValue()
        this.type = type
        isActive = active
    }

    constructor(buy: BuyModel) {
        id = buy.uid
        name = buy.product
        quantity = buy.quantity
        date = Date()
        unitaryValue = buy.price
        totalValue = getTotalValue()
        type = ExpensesType.BUY
        isActive = buy.isActive
    }

    constructor(bill: BillModel) {
        id = bill.uid
        name = bill.bill
        quantity = bill.quantity
        date = bill.date
        unitaryValue = bill.value
        totalValue = getTotalValue()
        type = ExpensesType.BILL
        isActive = bill.isActive
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

    val isBuy: Boolean?
        get() = type?.let { ExpensesType.isBuy(it) }

    val isBill: Boolean?
        get() = type?.let { ExpensesType.isBill(it) }

    fun update(name: String?, quantity: Int, date: Date?, unitaryValue: Double, isActive: Boolean) {
        this.name = name
        this.quantity = quantity
        this.date = date
        this.unitaryValue = unitaryValue
        this.isActive = isActive
    }

    fun equalsTo(expenseEntity: ExpenseEntity): Boolean {
        return type == expenseEntity.type &&
                name == expenseEntity.name &&
                quantity == expenseEntity.quantity &&
                unitaryValue == expenseEntity.unitaryValue
    }
}