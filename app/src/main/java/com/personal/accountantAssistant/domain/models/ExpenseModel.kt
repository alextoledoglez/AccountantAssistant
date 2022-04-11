package com.personal.accountantAssistant.domain.models

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.extensions.DEFAULT_ACTIVE_STATUS
import com.personal.accountantAssistant.extensions.DEFAULT_QUANTITY_VALUE
import com.personal.accountantAssistant.extensions.ZERO
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
data class ExpenseModel(
    val id: Long = Long.ZERO,
    val name: String? = null,
    val quantity: Int = Int.ZERO,
    val date: Date? = null,
    val unitaryValue: BigDecimal = BigDecimal.ZERO,
    var totalValue: BigDecimal = BigDecimal.ZERO,
    val type: ExpensesType? = null,
    val isActive: Boolean = false
) : Parcelable {

    init {
        totalValue = calculateTotalValue()
    }

    constructor(product: String?, type: ExpensesType?) : this(
        name = product,
        quantity = Int.DEFAULT_QUANTITY_VALUE,
        date = Date(),
        unitaryValue = BigDecimal.ZERO,
        totalValue = BigDecimal.ZERO,
        type = type,
        isActive = Boolean.DEFAULT_ACTIVE_STATUS
    )

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
