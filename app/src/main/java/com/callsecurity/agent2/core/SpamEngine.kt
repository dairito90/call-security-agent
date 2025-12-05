package com.callsecurity.agent2.core

object SpamEngine {

    private val knownSpamPatterns = listOf(
        Regex("^800\\d{4}$"),
        Regex("^833\\d{4}$"),
        Regex("^844\\d{4}$"),
        Regex(".*(insurance|warranty|loan|credit).*", RegexOption.IGNORE_CASE)
    )

    private val recentNumbers = mutableListOf<String>()

    fun scoreNumber(phone: String): Int {
        var score = 0

        if (knownSpamPatterns.any { it.containsMatchIn(phone) }) score += 40
        if (isSpoofed(phone)) score += 30
        if (isRepeated(phone)) score += 30

        return score.coerceIn(0, 100)
    }

    private fun isSpoofed(phone: String): Boolean {
        // Local spoof detection (same first 6 digits as device number)
        return phone.take(6) == "999000" // Placeholder logic
    }

    private fun isRepeated(phone: String): Boolean {
        recentNumbers.add(phone)
        if (recentNumbers.size > 10) recentNumbers.removeAt(0)

        return recentNumbers.count { it == phone } >= 3
    }
}
