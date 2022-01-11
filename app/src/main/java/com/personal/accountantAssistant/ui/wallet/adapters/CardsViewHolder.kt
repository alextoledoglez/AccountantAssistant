package com.personal.accountantAssistant.ui.wallet.adapters

import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.core.extensions.orZero
import com.personal.accountantAssistant.databinding.CardItemListBinding
import com.personal.accountantAssistant.ui.wallet.models.CardModel

class CardsViewHolder(private val binding: CardItemListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(card: CardModel) {
        with(binding) {
            title.text = card.title
            value.text = card.value.orZero().toString()
            scActive.isChecked = card.isEnabled
        }
    }

}