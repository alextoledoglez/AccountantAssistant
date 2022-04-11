package com.personal.accountantAssistant.data.enums

import com.personal.accountantAssistant.data.mappers.toEntity
import com.personal.accountantAssistant.data.mappers.toExpenseModel

enum class BillsEnum(var value: String) {
    WATER("ÁGUA"),
    ENERGY("ENERGIA"),
    INTERNET("INTERNET"),
    PHONE("TELEFONE"),
    MOBILE("MÓVEL"),
    CREDIT_CARD("CARTÃO DE CRÉDITO");

    companion object {
        fun toBillsEntities() = values().map { it.toExpenseModel().toEntity() }
    }
}