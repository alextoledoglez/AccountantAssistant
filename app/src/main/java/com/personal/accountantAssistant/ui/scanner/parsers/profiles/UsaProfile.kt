package com.personal.accountantAssistant.ui.scanner.parsers.profiles

import com.personal.accountantAssistant.ui.scanner.parsers.ScanParsingProfile
import java.util.Locale

internal object UsaProfile {

    private val AMOUNT_KEYWORDS = setOf(
        "AMOUNT DUE",
        "TOTAL DUE",
        "BALANCE DUE",
        "TOTAL",
        "AMOUNT"
    )

    private val DUE_DATE_KEYWORDS = setOf(
        "DUE DATE",
        "PAY BY",
        "PAYMENT DUE",
        "DUE"
    )

    private val TOTAL_KEYWORDS = setOf(
        "TOTAL",
        "SUBTOTAL",
        "DISCOUNT"
    )

    private val UNIT_KEYWORDS = setOf(
        "EA", "UNIT", "LB", "OZ", "GAL", "ML", "L", "PK", "BOX"
    )

    private val BANNED_TOKENS = setOf(
        "EXP", "EXPIRY", "LOT", "CODE",
        "REF", "ITEM", "DISCOUNT",
        "STORE", "REGISTER", "TAX ID"
    )

    private val KNOWN_PROVIDERS = setOf(
        "AT&T", "VERIZON", "T-MOBILE",
        "COMCAST", "XFINITY", "SPECTRUM",
        "DUKE ENERGY", "PG&E"
    )

    private val BILL_STRONG_KEYWORDS = setOf(
        "INVOICE", "BILL", "STATEMENT", "DOCUMENT"
    )

    private val DATE_PATTERNS = listOf(
        Regex("""\b(\d{2}/\d{2}/\d{2,4})\b"""),
        Regex("""\b(\d{2}-\d{2}-\d{2,4})\b""")
    )

    val profile = ScanParsingProfile(
        locale = Locale.US,
        currencySymbols = setOf("$", "USD"),
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