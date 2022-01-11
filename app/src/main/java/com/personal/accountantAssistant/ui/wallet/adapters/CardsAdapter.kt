package com.personal.accountantAssistant.ui.wallet.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.CardItemListBinding
import com.personal.accountantAssistant.ui.wallet.models.CardModel

class CardsAdapter : ListAdapter<CardModel, CardsViewHolder>(CardModel.DIFF_UTIL_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CardsViewHolder(
        CardItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}