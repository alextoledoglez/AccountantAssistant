package com.personal.accountantAssistant.adapters

import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.BuysItemListBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.utils.MenuHelper

class BuysViewHolderData(
    val binding: BuysItemListBinding,
    private val onItemClick: (model: ExpenseModel) -> Unit,
    private val notifyChanged: (index: Int, model: ExpenseModel) -> Unit,
    private val notifyRemoved: (index: Int, model: ExpenseModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ExpenseModel) {
        with(binding) {
            //INITIALIZE
            MenuHelper.initializeBuysOptions()
            //DETAILS
            name.text = model.name
            value.text = toFormattedValue(model)
            //ACTIONS
            scActive.apply {
                isChecked = model.isActive.orFalse()
                setOnClickListener { onActiveItemClick(bindingAdapterPosition, isChecked, model) }
            }
            ibDelete.setOnClickListener { notifyRemoved(bindingAdapterPosition, model) }
            itemView.setOnClickListener { onItemClick(model) }
            setRowForeground()
        }
    }

    private fun onActiveItemClick(position: Int, isActive: Boolean = true, model: ExpenseModel) {
        model.apply { this.isActive = isActive }
        notifyChanged(position, model)
    }

    private fun setRowForeground() {
        val isActive = binding.scActive.isChecked
        val textColor = binding.root.context.getColor(
            if (isActive) R.color.fontColor else R.color.disableFontColor
        )
        binding.apply {
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
}