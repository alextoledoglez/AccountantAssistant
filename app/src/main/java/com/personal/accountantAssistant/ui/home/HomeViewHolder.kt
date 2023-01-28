package com.personal.accountantAssistant.ui.home

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.databinding.HomeItemListBinding
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.extensions.setup
import com.personal.accountantAssistant.extensions.setupImageWithColor
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.extensions.toLayoutInflater

class HomeViewHolder(
    private val binding: HomeItemListBinding,
    private val onClickListener: (drawableRes: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: DashboardItemModel) {
        with(binding) {
            ivCardImage.setupImageWithColor(model.drawableRes, model.color)
            ivCardImage.isVisible = true
            tvCardTitle.setup(model.text, model.color)
            tvCardSubtitle.setup(model.value.abs().toCurrencyMaskedStr(), model.color)
            binding.root.setOnClickListener { onClickListener(model.drawableRes) }
        }
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            onClickListener: (drawableRes: Int) -> Unit
        ) = HomeViewHolder(
            binding = HomeItemListBinding.inflate(parent.toLayoutInflater(), parent, false),
            onClickListener = onClickListener
        )
    }
}