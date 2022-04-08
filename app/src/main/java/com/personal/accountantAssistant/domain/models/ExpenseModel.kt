package com.personal.accountantAssistant.domain.models

import android.os.Parcelable
import android.text.Editable
import androidx.recyclerview.widget.DiffUtil
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.extensions.*
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
data class ExpenseModel(
    var id: Long = 0,
    var name: String? = null,
    var quantity: Int = 0,
    var date: Date? = null,
    var unitaryValue: BigDecimal = BigDecimal.ZERO,
    var totalValue: BigDecimal = BigDecimal.ZERO,
    var type: ExpensesType? = null,
    var isActive: Boolean = false
) : Parcelable {

    init {
        totalValue = calculateTotalValue()
    }

    constructor(product: String?, type: ExpensesType?) : this() {
        this.name = product
        quantity = Int.DEFAULT_QUANTITY_VALUE
        date = Date()
        unitaryValue = BigDecimal.ZERO
        totalValue = BigDecimal.ZERO
        this.type = type
        isActive = Boolean.DEFAULT_ACTIVE_STATUS
    }

    fun update(
        name: Editable?,
        quantity: Editable?,
        date: Editable?,
        unitaryValue: Editable?,
        isActive: Boolean
    ) {
        this.name = name.toString()
        this.quantity = quantity.toInt()
        this.date = date.toDate()
        this.unitaryValue = unitaryValue.toCurrencyBigDecimal()
        this.isActive = isActive
    }

    fun calculateTotalValue(): BigDecimal = unitaryValue.multiply(quantity.toBigDecimal())

    companion object {
        val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<ExpenseModel>() {
            override fun areItemsTheSame(oldItem: ExpenseModel, newItem: ExpenseModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ExpenseModel, newItem: ExpenseModel) =
                oldItem.name == newItem.name &&
                        oldItem.quantity == newItem.quantity &&
                        oldItem.date == newItem.date &&
                        oldItem.unitaryValue == newItem.unitaryValue &&
                        oldItem.totalValue == newItem.totalValue &&
                        oldItem.type == newItem.type &&
                        oldItem.isActive == newItem.isActive
        }
    }
}
