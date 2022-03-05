package com.personal.accountantAssistant.domain.models.wallet

import androidx.recyclerview.widget.DiffUtil
import java.math.BigDecimal

data class CardModel(
    val company: String,
    val name: String,
    val password: Number,
    val value: BigDecimal,
    val isEnabled: Boolean
) {
    companion object {
        val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<CardModel>() {
            override fun areItemsTheSame(oldItem: CardModel, newItem: CardModel) =
                oldItem.company == newItem.company && oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: CardModel, newItem: CardModel) =
                oldItem == newItem
        }
    }
}
