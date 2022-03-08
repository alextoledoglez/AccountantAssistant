package com.personal.accountantAssistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.CardItemListBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.utils.EditableTextsUtils.contains
import java.util.stream.Collectors

class CardsListAdapter(
    private val onUpdate: (model: CardModel) -> Unit,
    private val onDelete: (model: CardModel) -> Unit,
    private val onClick: (model: CardModel) -> Unit,
) : ListAdapter<CardModel, CardsViewHolderData>(CardModel.DIFF_UTIL_CALLBACK), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CardsViewHolderData(
        CardItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onUpdate,
        onDelete,
        onClick
    )

    override fun onBindViewHolder(holder: CardsViewHolderData, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterStr = charSequence.toString()
                val list = if (filterStr.isEmpty()) currentList else {
                    currentList.stream().filter {
                        contains(it.company, filterStr) || contains(it.name, filterStr)
                    }?.collect(Collectors.toList())
                }
                return FilterResults().also { it.values = list }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                submitList(filterResults?.values as ArrayList<CardModel>?)
                notifyDataSetChanged()
            }
        }
    }
}