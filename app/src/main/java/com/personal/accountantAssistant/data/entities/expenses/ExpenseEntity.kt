package com.personal.accountantAssistant.data.entities.expenses

import android.text.Editable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personal.accountantAssistant.data.enums.expenses.ExpensesType
import com.personal.accountantAssistant.domain.models.bills.BillModel
import com.personal.accountantAssistant.domain.models.buys.BuyModel
import com.personal.accountantAssistant.extensions.toCurrencyBigDecimal
import com.personal.accountantAssistant.utils.DateUtils
import java.io.Serializable
import java.math.BigDecimal
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
    var unitaryValue: BigDecimal = BigDecimal.ZERO

    @ColumnInfo(name = "total_value")
    var totalValue: BigDecimal = BigDecimal.ZERO

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
        unitaryValue: BigDecimal,
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
    fun getTotalValue(): BigDecimal {
        totalValue = unitaryValue.multiply(quantity.toBigDecimal())
        return totalValue
    }

    val isBuy: Boolean?
        get() = type?.let { ExpensesType.isBuy(it) }

    val isBill: Boolean?
        get() = type?.let { ExpensesType.isBill(it) }

    fun update(
        name: Editable?,
        quantity: Editable?,
        date: Editable?,
        unitaryValue: Editable?,
        isActive: Boolean
    ) {
        this.name = name.toString()
        this.quantity = quantity.toString().toInt()
        this.date = DateUtils.toDate(date.toString())
        this.unitaryValue = unitaryValue.toCurrencyBigDecimal()
        this.isActive = isActive
    }

    fun equalsTo(expenseEntity: ExpenseEntity): Boolean {
        return type == expenseEntity.type &&
                name == expenseEntity.name &&
                quantity == expenseEntity.quantity &&
                unitaryValue == expenseEntity.unitaryValue
    }
}