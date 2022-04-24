package com.personal.accountantAssistant.domain.models

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import com.personal.accountantAssistant.R

data class MenuItemModel(
    val id: Int,
    @DrawableRes val icon: Int,
    @StringRes val text: Int,
    @ColorRes var color: Int = R.color.blackColor,
) {
    companion object {
        val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<MenuItemModel>() {
            override fun areItemsTheSame(oldItem: MenuItemModel, newItem: MenuItemModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: MenuItemModel, newItem: MenuItemModel
            ) = oldItem == newItem
        }
    }
}
