package com.personal.accountantAssistant.domain.models.home

data class SummaryModel(
    val available: SummaryItemModel?,
    val expenses: SummaryItemModel?,
    var gainOrNeeded: SummaryItemModel?,
)
