package com.personal.accountantAssistant.extensions

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.data.enums.ListNotifyTypes

fun <T, V : RecyclerView.ViewHolder?> ListAdapter<T, V>.notify(type: ListNotifyTypes, index: Int) {
    when (type) {
        ListNotifyTypes.INSERT -> notifyItemInserted(index)
        ListNotifyTypes.INSERT_ALL -> notifyItemRangeInserted(index, currentList.size)
        ListNotifyTypes.UPDATE -> notifyItemChanged(index)
        ListNotifyTypes.ACTIVE_ALL -> notifyItemRangeChanged(index, currentList.size)
        ListNotifyTypes.DELETE -> notifyItemRemoved(index)
        ListNotifyTypes.DELETE_ALL -> notifyItemRangeRemoved(index, itemCount)
    }
}