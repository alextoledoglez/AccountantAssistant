package com.personal.accountantAssistant.ui.menu

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.databinding.MenuItemListBinding
import com.personal.accountantAssistant.domain.models.MenuItemModel
import com.personal.accountantAssistant.extensions.setup
import com.personal.accountantAssistant.extensions.toLayoutInflater

class MenuViewHolder(private val binding: MenuItemListBinding) : RecyclerView.ViewHolder(
    binding.root
) {

    fun bind(model: MenuItemModel) {
        with(binding) {
            ivIcon.setup(model.icon, model.color)
            tvTitle.setup(model.text, model.color)
        }
    }

    companion object {
        fun newInstance(parent: ViewGroup) = MenuViewHolder(
            binding = MenuItemListBinding.inflate(parent.toLayoutInflater(), parent, false)
        )
    }
}