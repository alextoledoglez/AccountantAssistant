package com.personal.accountantAssistant.adapters

import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.CardItemListBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.utils.MenuHelper.initializeWalletOptions

class CardsViewHolderData(
    private val binding: CardItemListBinding,
    private val onClick: (model: CardModel) -> Unit,
    private val notifyItemChanged: (position: Int, model: CardModel) -> Unit,
    private val notifyItemRemoved: (position: Int, model: CardModel) -> Unit,
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
                setOnClickListener { setActive(model, isChecked) }
            }
            ibDelete.setOnClickListener { notifyItemRemoved(bindingAdapterPosition, model) }
            itemView.setOnClickListener { onClick(model) }
            setRowForeground()
        }
    }

    private fun setActive(model: CardModel, isChecked: Boolean) {
        model.apply { isActive = isChecked }
        notifyItemChanged(bindingAdapterPosition, model)
    }

    private fun setRowForeground() {
        val isActive = binding.scActive.isChecked
        val textColor = binding.root.context.getColor(
            if (isActive) R.color.fontColor else R.color.disableFontColor
        )
        binding.apply {
            tvCompany.setTextColor(textColor)
            tvName.setTextColor(textColor)
            tvValue.setTextColor(textColor)
        }
        val chipColor = binding.root.context.getColor(
            if (isActive) R.color.chipColor else R.color.disableChipColor
        )
        binding.ivChip.setColorFilter(chipColor, android.graphics.PorterDuff.Mode.MULTIPLY)
    }
}