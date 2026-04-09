package com.personal.accountantAssistant.ui.scanner.parsers.profiles

import com.personal.accountantAssistant.ui.scanner.parsers.ScanParsingProfile
import java.util.Locale

internal object SpainProfile {

    private val AMOUNT_KEYWORDS = setOf(
        "TOTAL A PAGAR",
        "IMPORTE",
        "IMPORTE TOTAL",
        "TOTAL",
        "TOTAL FACTURA"
    )

    private val DUE_DATE_KEYWORDS = setOf(
        "VENCIMIENTO",
        "FECHA DE VENCIMIENTO",
        "PAGO HASTA",
        "FECHA LIMITE",
        "FECHA LÍMITE"
    )

    private val TOTAL_KEYWORDS = setOf(
        "TOTAL",
        "SUBTOTAL",
        "DESCUENTO"
    )

    private val UNIT_KEYWORDS = setOf(
        "UN", "UND", "KG", "G", "GR", "ML", "L", "LT", "PAQ", "CAJA"
    )

    private val BANNED_TOKENS = setOf(
        "CADUCIDAD", "LOTE", "COD", "CÓDIGO",
        "REF", "ITEM", "DESCUENTO",
        "NIF", "TIENDA", "CAJA", "SUCURSAL"
    )

    private val KNOWN_PROVIDERS = setOf(
        "IBERDROLA", "ENDESA", "REPSOL",
        "MOVISTAR", "ORANGE", "VODAFONE",
        "JAZZTEL", "AGBAR"
    )

    private val BILL_STRONG_KEYWORDS = setOf(
        "FACTURA", "RECIBO", "DOCUMENTO"
    )

    private val DATE_PATTERNS = listOf(
        Regex("""\b(\d{2}/\d{2}/\d{2,4})\b"""),
        Regex("""\b(\d{2}-\d{2}-\d{2,4})\b""")
    )

    val profile = ScanParsingProfile(
        locale = Locale("es", "ES"),
        currencySymbols = setOf("€", "EUR"),
        amountKeywords = AMOUNT_KEYWORDS,
        dueDateKeywords = DUE_DATE_KEYWORDS,
        totalKeywords = TOTAL_KEYWORDS,
        unitKeywords = UNIT_KEYWORDS,
        bannedTokens = BANNED_TOKENS,
        knownProviders = KNOWN_PROVIDERS,
        datePatterns = DATE_PATTERNS,
        billStrongKeywords = BILL_STRONG_KEYWORDS
    )
}