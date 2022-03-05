package com.personal.accountantAssistant.utils

object NumberUtils {
    private const val ZERO_STR = "0"
    private const val ONE_STR = "1"
    fun toBoolean(numericString: String?): Boolean {
        var result = false
        if (numericString == ZERO_STR) {
            result = false
        } else if (numericString == ONE_STR) {
            result = true
        }
        return result
    }
}