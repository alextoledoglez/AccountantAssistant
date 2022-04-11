package com.personal.accountantAssistant.data.enums

import com.personal.accountantAssistant.data.mappers.toCardEntity

enum class DefaultCardsEnum(val company: String, val title: String) {
    BANK_ACCOUNT("BANK", "ACCOUNT");

    companion object {
        fun toCardsEntities() = values().map { it.toCardEntity() }
    }
}