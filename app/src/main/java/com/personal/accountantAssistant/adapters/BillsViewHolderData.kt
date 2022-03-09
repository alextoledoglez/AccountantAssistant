package com.personal.accountantAssistant.adapters

import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.BillsItemListBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.EQUAL_OPERATOR
import com.personal.accountantAssistant.extensions.TIMES
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.utils.DateUtils
import com.personal.accountantAssistant.utils.MenuHelper

class BillsViewHolderData(
    val binding: BillsItemListBinding,
    private val onUpdate: (model: ExpenseModel) -> Unit,
    private val onDelete: (model: ExpenseModel) -> Unit,
    private val onClick: (model: ExpenseModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ExpenseModel) {
        with(binding) {
            //INITIALIZE
            MenuHelper.initializeBillsOptions()
            date.text = DateUtils.toString(model.date)
            //DETAILS
            name.text = model.name
            value.text = toFormattedValue(model)
            //ACTIONS
            activeAction.isChecked = model.isActive.orFalse()
            activeAction.setOnClickListener { setActive(model, activeAction.isChecked) }
            deleteAction.setOnClickListener { onDelete(model) }
            itemView.setOnClickListener { onClick(model) }
            setRowForeground()
        }
    }

    private fun setActive(model: ExpenseModel, isChecked: Boolean = true) {
        model.apply {
            isActive = isChecked
            onUpdate(this)
        }
    }

    private fun setRowForeground() {
        val isActive = binding.activeAction.isChecked
        val textColor = binding.root.context.getColor(
            if (isActive) R.color.fontColor else R.color.disableFontColor
        )
        binding.apply {
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
}