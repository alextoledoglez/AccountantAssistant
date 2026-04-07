package com.personal.accountantAssistant.domain.models

import android.os.Parcelable
import android.text.Editable
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

    fun toFormatExpenseValue(showDate: Boolean): String {
        val unitaryPriceStr = unitaryValue.toCurrencyMaskedStr()
        val totalPriceStr = calculateTotalValue().toCurrencyMaskedStr()
        return if (showDate) {
            val quantityStr = quantity.toString() + String.TIMES
            "$quantityStr${unitaryPriceStr}${String.EQUAL_OPERATOR}${totalPriceStr}"
        } else {
            val quantityStr = quantity.toString() + String.UNITY
            "$quantityStr${String.MULTIPLY_OPERATOR}${unitaryPriceStr}${String.EQUAL_OPERATOR}${totalPriceStr}"
        }
    }

}
