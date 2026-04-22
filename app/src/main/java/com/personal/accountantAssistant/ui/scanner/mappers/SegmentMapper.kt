package com.personal.accountantAssistant.ui.scanner.mappers

object SegmentMapper {
    fun map(segment: String): String = when (segment) {
        "1" -> "Prefeituras"
        "2" -> "Saneamento"
        "3" -> "Energia elétrica e gás"
        "4" -> "Telefone"
        "5" -> "Órgãos governamentais"
        "6" -> "Carnês e assemelhados"
        "7" -> "Multas de trânsito"
        "9" -> "Uso exclusivo bancário"
        else -> "Desconhecido"
    }
}