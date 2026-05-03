package com.personal.accountantAssistant.domain.models

import android.os.Parcelable
import android.text.Editable
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyBigDecimal
import com.personal.accountantAssistant.extensions.toDate
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
data class CardModel(
    val id: Long = 0,
    val company: String = String.EMPTY,
    val name: String = String.EMPTY,
    val date: Date? = null,
    val password: String = String.EMPTY,
    val usedValue: BigDecimal = BigDecimal.ZERO,
    val availableValue: BigDecimal = BigDecimal.ZERO,
    val limitValue: BigDecimal = BigDecimal.ZERO,
    val isActive: Boolean = false
) : Parcelable {

    fun update(
        company: Editable?,
        name: Editable?,
        date: Editable?,
        password: Editable?,
        availableValue: Editable?,
        limitValue: Editable?,
        isChecked: Boolean?
    ) = copy(
        company = company.toString(),
        name = name.toString(),
        date = date.toDate(),
        password = password.toString(),
        availableValue = availableValue.toCurrencyBigDecimal(),
        usedValue = getUsedCardValue().toString().toCurrencyBigDecimal(),
        limitValue = limitValue.toCurrencyBigDecimal(),
        isActive = isChecked.orFalse(),
    )

    private fun getUsedCardValue() = limitValue.minus(availableValue)

}
