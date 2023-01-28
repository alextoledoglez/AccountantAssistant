package com.personal.accountantAssistant.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.domain.models.DashboardItemModel

class HomeListAdapter(private val onClickListener: (drawableRes: Int) -> Unit) :
    ListAdapter<DashboardItemModel, HomeViewHolder>(DashboardItemModel.DIFF_UTIL_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HomeViewHolder.newInstance(parent, onClickListener)

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}