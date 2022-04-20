package com.personal.accountantAssistant.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


fun RecyclerView.destroyAdapter() {
    adapter = null
}

fun RecyclerView.setGridLayoutAdapter(listAdapter: ListAdapter<*, *>, spanCount: Int = 1) {
    adapter = listAdapter
    layoutManager = GridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)
}