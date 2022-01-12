package com.personal.accountantAssistant.domain.models.wallet

import androidx.recyclerview.widget.DiffUtil

data class CardModel(
    val company: String,
    val name: String,
    val password: Number,
    val value: Float,
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
