package com.personal.accountantAssistant.extensions

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

fun SwipeRefreshLayout.stopRefreshing() {
    this.isRefreshing = false
}