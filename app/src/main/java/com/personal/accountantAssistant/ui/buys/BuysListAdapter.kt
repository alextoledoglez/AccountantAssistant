package com.personal.accountantAssistant.ui.buys

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.bases.adapters.ListAdapterChanges
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.containStr
import com.personal.accountantAssistant.extensions.settingFilter
import java.util.function.Predicate

class BuysListAdapter(
    private val adapterChanges: ListAdapterChanges<ExpenseModel>
) : ListAdapter<ExpenseModel, BuysViewHolder>(ExpenseModel.DIFF_UTIL_CALLBACK), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BuysViewHolder.newInstance(parent, adapterChanges)

    override fun onBindViewHolder(holder: BuysViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getFilter(): Filter {
        return this.settingFilter(currentList, ::filter, ::submitList)
    }

    private fun filter(text: String) = Predicate<ExpenseModel> { it.name.containStr(text) }
}