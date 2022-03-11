package com.personal.accountantAssistant.domain.models

import android.os.Parcelable
import android.text.Editable
import androidx.recyclerview.widget.DiffUtil
import com.personal.accountantAssistant.extensions.DEFAULT_QUANTITY_VALUE
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.toCurrencyBigDecimal
import com.personal.accountantAssistant.extensions.toInt
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class CardModel(
    var id: Long = 0,
    var company: String = String.EMPTY,
    var name: String = String.EMPTY,
    var password: Int = Int.DEFAULT_QUANTITY_VALUE,
    var value: BigDecimal = BigDecimal.ZERO,
    var isActive: Boolean = false
) : Parcelable {

    fun update(
        company: Editable?,
        name: Editable?,
        password: Editable?,
        value: Editable?,
        isActive: Boolean
    ) {
        this.company = company.toString()
        this.name = name.toString()
        this.password = password.toInt()
        this.value = value.toCurrencyBigDecimal()
        this.isActive = isActive
    }

    fun updateWith(model: CardModel) {
        this.company = model.company
        this.name = model.name
        this.password = model.password
        this.value = model.value
        this.isActive = model.isActive
    }

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
