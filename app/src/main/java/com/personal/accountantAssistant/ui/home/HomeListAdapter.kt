package com.personal.accountantAssistant.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.HomeItemListBinding
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.extensions.toLayoutInflater

class HomeListAdapter : ListAdapter<DashboardItemModel, HomeViewHolder>(
    DashboardItemModel.DIFF_UTIL_CALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeViewHolder(
        HomeItemListBinding.inflate(parent.context.toLayoutInflater(), parent, false)
    )

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}