package com.personal.accountantAssistant.domain.models.home

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil

data class DashboardItemModel(
    @DrawableRes val drawableRes: Int?,
    @StringRes val strResource: Int?,
    @ColorRes var colorResource: Int?,
    val value: Double?
) {
    companion object {
        val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<DashboardItemModel>() {
            override fun areItemsTheSame(oldItem: DashboardItemModel, newItem: DashboardItemModel) =
                oldItem.drawableRes == newItem.drawableRes

            override fun areContentsTheSame(
                oldItem: DashboardItemModel, newItem: DashboardItemModel
            ) = oldItem == newItem

        }
    }
}
