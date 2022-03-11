package com.personal.accountantAssistant.extensions

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.data.enums.ListNotifyTypes

fun <T, V : RecyclerView.ViewHolder?> ListAdapter<T, V>.notify(
    type: ListNotifyTypes, position: Int
) {
    when (type) {
        ListNotifyTypes.INSERT -> notifyItemInserted(position)
        ListNotifyTypes.INSERT_ALL -> notifyItemRangeInserted(position, currentList.size)
        ListNotifyTypes.UPDATE -> notifyItemChanged(position)
        ListNotifyTypes.UPDATE_ALL -> notifyItemRangeChanged(position, currentList.size)
        ListNotifyTypes.DELETE -> notifyItemRemoved(position)
        ListNotifyTypes.DELETE_ALL -> notifyItemRangeRemoved(position, currentList.size)
    }
}