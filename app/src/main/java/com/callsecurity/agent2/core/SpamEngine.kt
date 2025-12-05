package com.callsecurity.agent2.core

object SpamEngine {

    // Common spam patterns (expandable)
    private val spamPatterns = listOf(
        Regex("^888"),        // fake toll-free scammers
        Regex("^800"),
        Regex("^833"),
        Regex(".*(loan|credit|warranty).*", RegexOption.IGNORE_CASE)
    )

    // Numbers that call too frequently
    private val frequencyMap = HashMap<String, Int>()

    fun isSpam(number: String): Boolean {
        if (number.isBlank()) return false

        val score = getSpamScore(number)
        return score >= 60
    }

    fun getSpamScore(number: String): Int {
        var score = 0

        // Pattern check
        if (spamPatterns.any { it.containsMatchIn(number) }) {
            score += 40
        }

        // Frequency check
        val count = frequencyMap.getOrDefault(number, 0) + 1
        frequencyMap[number] = count

        if (count > 3) score += 30

        // Spoof check (same prefix as user)
        if (number.length >= 6) {
            val prefix = number.take(6)
            if (prefix == getUserPrefix()) {
                score += 20
            }
        }

        return score.coerceIn(0, 100)
    }

    private fun getUserPrefix(): String {
        // simulated user prefix (replace later with telephony)
        return "786555"
    }
}
