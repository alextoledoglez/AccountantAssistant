package com.personal.accountantAssistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.CardItemListBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.settingFilter

import com.personal.accountantAssistant.utils.EditableTextsUtils
import java.util.function.Predicate

class CardsListAdapter(
    private val onClick: (model: CardModel) -> Unit,
    private val onItemChanged: (model: CardModel) -> Unit,
    private val onItemRemoved: (model: CardModel) -> Unit,
) : ListAdapter<CardModel, CardsViewHolder>(CardModel.DIFF_UTIL_CALLBACK), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CardsViewHolder(
        CardItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick,
        onItemChanged,
        onItemRemoved
    )

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getFilter(): Filter {
        return this.settingFilter(currentList, ::filter, ::submitList)
    }

    private fun filter(text: String) = Predicate<CardModel> {
        EditableTextsUtils.contains(it.value.toString(), text) ||
                EditableTextsUtils.contains(it.password, text) ||
                EditableTextsUtils.contains(it.company, text) ||
                EditableTextsUtils.contains(it.name, text)
    }
}