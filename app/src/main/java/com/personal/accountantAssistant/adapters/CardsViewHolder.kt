package com.personal.accountantAssistant.adapters

import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.CardItemListBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.getCompatColor
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr

class CardsViewHolder(
    private val binding: CardItemListBinding,
    private val onEditCard: (model: CardModel) -> Unit,
    private val onActiveCard: (model: CardModel) -> Unit,
    private val onRemoveCard: (model: CardModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: CardModel) {
        with(binding) {
            //DETAILS
            tvCompany.text = model.company
            tvName.text = model.name
            tvValue.text = model.value.toCurrencyMaskedStr()
            //ACTIONS
            scActive.apply {
                isChecked = model.isActive.orFalse()
                setOnClickListener {
                    val switchedModel = model.copy(isActive = !model.isActive)
                    onActiveCard(switchedModel)
                    setActiveRow()
                }
            }
            itemView.setOnClickListener { onEditCard(model) }
            ibDelete.setOnClickListener { onRemoveCard(model) }
            setActiveRow()
        }
    }

    private fun setActiveRow() {
        val context = binding.root.context
        val isActive = binding.scActive.isChecked
        val textColor = context.getCompatColor(
            isActive, R.color.fontColor, R.color.disableFontColor
        )
        val chipColor = context.getCompatColor(
            isActive, R.color.chipColor, R.color.disableChipColor
        )
        binding.apply {
            tvCompany.setTextColor(textColor)
            tvName.setTextColor(textColor)
            tvValue.setTextColor(textColor)
            ivChip.setColorFilter(chipColor, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }
}