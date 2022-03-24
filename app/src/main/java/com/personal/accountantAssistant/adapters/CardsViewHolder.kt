package com.personal.accountantAssistant.adapters

import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.CardItemListBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.utils.MenuHelper.initializeWalletOptions

class CardsViewHolder(
    private val binding: CardItemListBinding,
    private val onClick: (model: CardModel) -> Unit,
    private val onItemChanged: (model: CardModel) -> Unit,
    private val onItemRemoved: (model: CardModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: CardModel) {
        with(binding) {
            //INITIALIZE
            initializeWalletOptions()
            //DETAILS
            tvCompany.text = model.company
            tvName.text = model.name
            tvValue.text = model.value.toCurrencyMaskedStr()
            //ACTIONS
            scActive.apply {
                isChecked = model.isActive.orFalse()
                setOnClickListener {
                    onItemChanged(model)
                    setActiveRow()
                }
            }
            ibDelete.setOnClickListener { onItemRemoved(model) }
            itemView.setOnClickListener { onClick(model) }
            setActiveRow()
        }
    }

    private fun setActiveRow() {
        val context = binding.root.context
        val isActive = binding.scActive.isChecked
        val textColor = context.getColor(
            if (isActive) R.color.fontColor else R.color.disableFontColor
        )
        val chipColor = context.getColor(
            if (isActive) R.color.chipColor else R.color.disableChipColor
        )
        binding.apply {
            tvCompany.setTextColor(textColor)
            tvName.setTextColor(textColor)
            tvValue.setTextColor(textColor)
            ivChip.setColorFilter(chipColor, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }
}