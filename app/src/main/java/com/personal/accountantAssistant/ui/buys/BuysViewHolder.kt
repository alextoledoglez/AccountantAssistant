package com.personal.accountantAssistant.ui.buys

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.adapters.ListAdapterChanges
import com.personal.accountantAssistant.databinding.BuysItemListBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*

class BuysViewHolder(
    val binding: BuysItemListBinding, private val adapterChanges: ListAdapterChanges<ExpenseModel>
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ExpenseModel) {
        with(binding) {
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
            value.setTextColor(textColor)
        }
    }

    private fun toFormattedValue(model: ExpenseModel): String {
        val quantityStr = model.quantity.toString() + String.UNITY
        val operator = String.MULTIPLY_OPERATOR
        val unitaryPriceStr = model.unitaryValue.toCurrencyMaskedStr()
        val totalPriceStr = model.calculateTotalValue().toCurrencyMaskedStr()
        return "$quantityStr$operator${unitaryPriceStr}${String.EQUAL_OPERATOR}${totalPriceStr}"
    }

    companion object {
        fun newInstance(
            parent: ViewGroup, adapterChanges: ListAdapterChanges<ExpenseModel>
        ) = BuysViewHolder(
            binding = BuysItemListBinding.inflate(parent.context.toLayoutInflater(), parent, false),
            adapterChanges = adapterChanges
        )
    }
}