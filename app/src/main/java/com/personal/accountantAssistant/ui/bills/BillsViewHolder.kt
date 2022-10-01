package com.personal.accountantAssistant.ui.bills

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.adapters.ListAdapterChanges
import com.personal.accountantAssistant.databinding.BillsItemListBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*

class BillsViewHolder(
    val binding: BillsItemListBinding, private val adapterChanges: ListAdapterChanges<ExpenseModel>
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ExpenseModel) {
        with(binding) {
            //INITIALIZE
            date.text = model.date.toDateStr()
            //DETAILS
            name.text = model.name
            value.text = toFormattedValue(model)
            //ACTIONS
            lytActions.scActive.apply {
                isChecked = model.isActive.orFalse()
                setOnClickListener {
                    val switchedModel = model.copy(isActive = !model.isActive)
                    adapterChanges.onActive(switchedModel)
                    setActiveRow()
                }
            }
            itemView.setOnClickListener { adapterChanges.onEdit(model) }
            lytActions.ibDelete.setOnClickListener { adapterChanges.onRemove(model) }
        }
        setActiveRow()
    }

    private fun setActiveRow() {
        with(binding) {
            val isActive = lytActions.scActive.isChecked
            val textColor = root.context.getCompatColor(
                isActive, R.color.fontColor, R.color.disableFontColor
            )
            name.setTextColor(textColor)
            date.setTextColor(textColor)
            value.setTextColor(textColor)
        }
    }

    private fun toFormattedValue(model: ExpenseModel): String {
        val quantityStr = model.quantity.toString() + String.TIMES
        val unitaryPriceStr = model.unitaryValue.toCurrencyMaskedStr()
        val totalPriceStr = model.calculateTotalValue().toCurrencyMaskedStr()
        return "$quantityStr${unitaryPriceStr}${String.EQUAL_OPERATOR}${totalPriceStr}"
    }

    companion object {
        fun newInstance(parent: ViewGroup, adapterChanges: ListAdapterChanges<ExpenseModel>) =
            BillsViewHolder(
                binding = BillsItemListBinding.inflate(
                    parent.context.toLayoutInflater(), parent, false
                ),
                adapterChanges = adapterChanges
            )
    }
}