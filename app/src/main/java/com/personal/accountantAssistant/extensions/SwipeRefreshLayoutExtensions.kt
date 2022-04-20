package com.personal.accountantAssistant.extensions

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

fun SwipeRefreshLayout.updateRefreshing(isRefreshing: Boolean) {
    if (this.isRefreshing != isRefreshing)
        this.isRefreshing = isRefreshing
}

fun SwipeRefreshLayout.stopRefreshing() {
    this.isRefreshing = false
}