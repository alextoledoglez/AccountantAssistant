package com.personal.accountantAssistant.ui.scanner.parsers

import java.util.Locale

internal data class ScanParsingProfile(
    val locale: Locale,
    val currencySymbols: Set<String>,
    val amountKeywords: Set<String>,
    val dueDateKeywords: Set<String>,
    val totalKeywords: Set<String>,
    val unitKeywords: Set<String>,
    val bannedTokens: Set<String>,
    val knownProviders: Set<String>,
    val datePatterns: List<Regex>,
    val billStrongKeywords: Set<String> = emptySet()
)