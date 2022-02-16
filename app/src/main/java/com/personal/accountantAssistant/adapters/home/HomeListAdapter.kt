package com.personal.accountantAssistant.adapters.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.HomeItemListBinding
import com.personal.accountantAssistant.domain.models.home.DashboardItemModel

class HomeListAdapter : ListAdapter<DashboardItemModel, HomeViewHolderData>(
    DashboardItemModel.DIFF_UTIL_CALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeViewHolderData(
        HomeItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: HomeViewHolderData, position: Int) {
        holder.bind(currentList[position])
    }

}