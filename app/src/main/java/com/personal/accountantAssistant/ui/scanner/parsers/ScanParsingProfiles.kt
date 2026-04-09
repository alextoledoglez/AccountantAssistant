package com.personal.accountantAssistant.ui.scanner.parsers

import com.personal.accountantAssistant.ui.scanner.parsers.profiles.BrazilProfile
import com.personal.accountantAssistant.ui.scanner.parsers.profiles.SpainProfile
import com.personal.accountantAssistant.ui.scanner.parsers.profiles.UsaProfile

internal object ScanParsingProfiles {

    val Brazil = BrazilProfile.profile
    val Spain = SpainProfile.profile
    val Usa = UsaProfile.profile

    /**
     * Returns the profile that matches the device locale.
     * Falls back to Brazil when the locale is not recognized.
     */
    fun defaultFor(language: String?, country: String?): ScanParsingProfile {
        val lang = language.orEmpty().lowercase()
        val ctry = country.orEmpty().uppercase()

        return when {
            lang == "pt" || ctry == "BR" -> Brazil
            lang == "es" || ctry == "ES" -> Spain
            lang == "en" || ctry == "US" -> Usa
            else -> Brazil
        }
    }

    /**
     * Detects the best matching profile from the scanned text content, regardless of device locale.
     * Scores each profile by counting its signals (currency symbols, providers, keywords) found in
     * the text. Falls back to Brazil if no profile has a clear signal.
     */
    fun detectFrom(text: String): ScanParsingProfile {
        if (text.isBlank()) return Brazil
        val upper = text.uppercase()
        val profiles = listOf(Brazil, Spain, Usa)
        return profiles.maxByOrNull { profile ->
            var score = 0
            if (profile.currencySymbols.any { upper.contains(it) }) score += 10
            score += profile.knownProviders.count { upper.contains(it) } * 8
            score += profile.amountKeywords.count { upper.contains(it) } * 3
            score += profile.dueDateKeywords.count { upper.contains(it) } * 3
            score += profile.billStrongKeywords.count { upper.contains(it) } * 2
            score
        } ?: Brazil
    }
}