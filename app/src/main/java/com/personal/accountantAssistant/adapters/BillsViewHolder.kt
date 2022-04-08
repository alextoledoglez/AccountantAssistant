package com.personal.accountantAssistant.adapters

import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.BillsItemListBinding
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.utils.DateUtils
import com.personal.accountantAssistant.utils.MenuHelper

class BillsViewHolder(
    val binding: BillsItemListBinding,
    private val onEditExpense: (model: ExpenseModel) -> Unit,
    private val onActiveExpense: (model: ExpenseModel) -> Unit,
    private val onRemoveExpense: (model: ExpenseModel) -> Unit,
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
            scActive.apply {
                isChecked = model.isActive.orFalse()
                setOnClickListener {
                    onActiveExpense(model)
                    setActiveRow()
                }
            }
            itemView.setOnClickListener { onEditExpense(model) }
            ibDelete.setOnClickListener { onRemoveExpense(model) }
        }
        setActiveRow()
    }

    private fun setActiveRow() {
        with(binding) {
            val isActive = scActive.isChecked
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
}