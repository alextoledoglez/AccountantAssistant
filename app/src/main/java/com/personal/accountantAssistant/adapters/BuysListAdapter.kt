package com.personal.accountantAssistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.BuysItemListBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.settingFilter
import com.personal.accountantAssistant.utils.EditableTextsUtils.contains
import java.util.function.Predicate

class BuysListAdapter(
    private val onEditExpense: (model: ExpenseModel) -> Unit,
    private val onActiveExpense: (model: ExpenseModel) -> Unit,
    private val onRemoveExpense: (model: ExpenseModel) -> Unit,
) : ListAdapter<ExpenseModel, BuysViewHolder>(ExpenseModel.DIFF_UTIL_CALLBACK), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BuysViewHolder(
        BuysItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onEditExpense,
        onActiveExpense,
        onRemoveExpense
    )

    override fun onBindViewHolder(holder: BuysViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getFilter(): Filter {
        return this.settingFilter(currentList, ::filter, ::submitList)
    }

    private fun filter(text: String) = Predicate<ExpenseModel> { contains(it.name, text) }
}