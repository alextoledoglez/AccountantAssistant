package com.personal.accountantAssistant.domain.models

import java.math.BigDecimal

data class WalletModel(
    val isAllChecked: Boolean,
    val isAnyChecked: Boolean,
    val total: BigDecimal,
    val cards: ArrayList<CardModel>
)
