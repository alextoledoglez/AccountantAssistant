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

    fun update(
        name: Editable?,
        quantity: Editable?,
        date: Editable?,
        value: Editable?,
        isChecked: Boolean?
    ) = copy(
        name = name.toString(),
        quantity = quantity.toInt(),
        date = date.toDate(),
        unitaryValue = value.toCurrencyBigDecimal(),
        isActive = isChecked.orFalse()
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
