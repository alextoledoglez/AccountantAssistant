package com.personal.accountantAssistant.ui.wallet

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.adapters.ListAdapterChanges
import com.personal.accountantAssistant.databinding.CardItemListBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.getCompatColor
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.extensions.toLayoutInflater

class CardsViewHolder(
    private val binding: CardItemListBinding,
    private val adapterChanges: ListAdapterChanges<CardModel>
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: CardModel) {
        with(binding) {
            //DETAILS
            tvCompany.text = model.company
            tvName.text = model.name
            tvValue.text = model.availableValue.toCurrencyMaskedStr()
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
            val context = root.context
            val isActive = lytActions.scActive.isChecked
            val textColor = context.getCompatColor(
                isActive, R.color.fontColor, R.color.disableFontColor
            )
            val chipColor = context.getCompatColor(
                isActive, R.color.chipColor, R.color.disableChipColor
            )
            tvCompany.setTextColor(textColor)
            tvName.setTextColor(textColor)
            tvValue.setTextColor(textColor)
            ivChip.setColorFilter(chipColor, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    companion object {
        fun newInstance(parent: ViewGroup, adapterChanges: ListAdapterChanges<CardModel>) =
            CardsViewHolder(
                binding = CardItemListBinding.inflate(
                    parent.context.toLayoutInflater(),
                    parent,
                    false
                ),
                adapterChanges = adapterChanges
            )
    }
}