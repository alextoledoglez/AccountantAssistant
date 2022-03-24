package com.personal.accountantAssistant.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.HomeItemListBinding
import com.personal.accountantAssistant.domain.models.DashboardItemModel

class HomeListAdapter : ListAdapter<DashboardItemModel, HomeViewHolder>(
    DashboardItemModel.DIFF_UTIL_CALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeViewHolder(
        HomeItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}