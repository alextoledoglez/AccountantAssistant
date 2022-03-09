package com.personal.accountantAssistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.BuysItemListBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.utils.EditableTextsUtils.contains
import java.util.stream.Collectors

class BuysListAdapter(
    private val onUpdate: (model: ExpenseModel) -> Unit,
    private val onDelete: (model: ExpenseModel) -> Unit,
    private val onClick: (model: ExpenseModel) -> Unit
) : ListAdapter<ExpenseModel, BuysViewHolderData>(ExpenseModel.DIFF_UTIL_CALLBACK), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BuysViewHolderData(
        BuysItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onUpdate,
        onDelete,
        onClick
    )

    override fun onBindViewHolder(holder: BuysViewHolderData, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterStr = charSequence.toString()
                val list = if (filterStr.isEmpty()) currentList else
                    currentList.stream().filter { contains(it.name, filterStr) }
                        ?.collect(Collectors.toList())
                return FilterResults().also { it.values = list }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                submitList(filterResults?.values as MutableList<ExpenseModel>?)
            }
        }
    }

}