package com.personal.accountantAssistant.domain.models.home

data class DashboardModel(
    val available: DashboardItemModel?,
    val expensesItems: ExpensesItemsModel?,
    var gainOrNeeded: DashboardItemModel?,
)
