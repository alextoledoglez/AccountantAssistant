package com.personal.accountantAssistant.ui.menu

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.domain.models.MenuItemModel

class MenuListAdapter : ListAdapter<MenuItemModel, MenuViewHolder>(
    MenuItemModel.DIFF_UTIL_CALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MenuViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}