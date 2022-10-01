package com.personal.accountantAssistant.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.domain.models.DashboardItemModel

class HomeListAdapter : ListAdapter<DashboardItemModel, HomeViewHolder>(
    DashboardItemModel.DIFF_UTIL_CALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HomeViewHolder.newInstance(parent)

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}