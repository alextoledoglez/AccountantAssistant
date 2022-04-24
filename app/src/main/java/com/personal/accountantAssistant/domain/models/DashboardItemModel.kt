package com.personal.accountantAssistant.domain.models

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import java.math.BigDecimal

data class DashboardItemModel(
    @DrawableRes val drawableRes: Int,
    val text: String,
    @ColorInt var color: Int,
    val value: BigDecimal
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
