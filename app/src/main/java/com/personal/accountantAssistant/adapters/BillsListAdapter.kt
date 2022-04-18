package com.personal.accountantAssistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.BillsItemListBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.containStr
import com.personal.accountantAssistant.extensions.settingFilter
import java.util.function.Predicate

class BillsListAdapter(
    private val onEditExpense: (model: ExpenseModel) -> Unit,
    private val onActiveExpense: (model: ExpenseModel) -> Unit,
    private val onRemoveExpense: (model: ExpenseModel) -> Unit,
) : ListAdapter<ExpenseModel, BillsViewHolder>(ExpenseModel.DIFF_UTIL_CALLBACK), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BillsViewHolder(
        BillsItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onEditExpense,
        onActiveExpense,
        onRemoveExpense
    )

    override fun onBindViewHolder(holder: BillsViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getFilter(): Filter {
        return this.settingFilter(currentList, ::filter, ::submitList)
    }

    private fun filter(text: String) = Predicate<ExpenseModel> { it.name.containStr(text) }
}