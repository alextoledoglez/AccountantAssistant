package com.personal.accountantAssistant.adapters

import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.BuysItemListBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.utils.MenuHelper

class BuysViewHolderData(
    val binding: BuysItemListBinding,
    private val onClick: (model: ExpenseModel) -> Unit,
    private val notifyItemChanged: (position: Int, model: ExpenseModel) -> Unit,
    private val notifyItemRemoved: (position: Int, model: ExpenseModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ExpenseModel) {
        with(binding) {
            //INITIALIZE
            MenuHelper.initializeBuysOptions()
            //DETAILS
            name.text = model.name
            value.text = toFormattedValue(model)
            //ACTIONS
            activeAction.apply {
                isChecked = model.isActive.orFalse()
                setOnClickListener { setActive(model, isChecked) }
            }
            deleteAction.setOnClickListener { notifyItemRemoved(bindingAdapterPosition, model) }
            itemView.setOnClickListener { onClick(model) }
            setRowForeground()
        }
    }

    private fun setActive(model: ExpenseModel, isChecked: Boolean = true) {
        model.apply { isActive = isChecked }
        notifyItemChanged(bindingAdapterPosition, model)
    }

    private fun setRowForeground() {
        val isActive = binding.activeAction.isChecked
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