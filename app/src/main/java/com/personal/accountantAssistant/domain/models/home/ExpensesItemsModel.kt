package com.personal.accountantAssistant.domain.models.home

data class ExpensesItemsModel(
    val daily: DashboardItemModel?,
    val buy: DashboardItemModel?,
    val bill: DashboardItemModel?,
    val total: DashboardItemModel?
)
