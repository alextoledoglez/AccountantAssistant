package com.personal.accountantAssistant.domain.models

import android.os.Parcelable
import android.text.Editable
import androidx.recyclerview.widget.DiffUtil
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyBigDecimal
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class CardModel(
    val id: Long = 0,
    val company: String = String.EMPTY,
    val name: String = String.EMPTY,
    val password: String = String.EMPTY,
    val value: BigDecimal = BigDecimal.ZERO,
    val isActive: Boolean = false
) : Parcelable {

    fun update(
        company: Editable?,
        name: Editable?,
        password: Editable?,
        value: Editable?,
        isChecked: Boolean?
    ) = copy(
        company = company.toString(),
        name = name.toString(),
        password = password.toString(),
        value = value.toCurrencyBigDecimal(),
        isActive = isChecked.orFalse(),
    )

    companion object {
        val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<CardModel>() {
            override fun areItemsTheSame(oldItem: CardModel, newItem: CardModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CardModel, newItem: CardModel) =
                oldItem.company == newItem.company &&
                        oldItem.name == newItem.name &&
                        oldItem.password == newItem.password &&
                        oldItem.value == newItem.value &&
                        oldItem.isActive == newItem.isActive
        }
    }
}
