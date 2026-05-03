package com.personal.accountantAssistant.ui.scanner.mappers

object BankCodeMapper {
    fun map(bankCode: String): String = when (bankCode) {
        "001" -> "Banco do Brasil"
        "033" -> "Santander"
        "104" -> "Caixa Econômica Federal"
        "237" -> "Bradesco"
        "341" -> "Itaú"
        "260" -> "NuBank"
        "077" -> "Inter"
        "212" -> "Original"
        else -> "Desconhecido"
    }
}