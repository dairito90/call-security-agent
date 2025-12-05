package com.callsecurity.agent2.core

object SpamEngine {

    // Very simple rules – you can expand this later
    private val spamNumbers = setOf(
        "8001234567",
        "8880000000"
    )

    fun isSpam(metadata: CallMetadata): Boolean {
        val digitsOnly = metadata.phoneNumber.filter { it.isDigit() }

        // block explicit bad numbers
        if (spamNumbers.contains(digitsOnly)) return true

        // suspicious if number is too short
        if (digitsOnly.length in 1..6) return true

        // default: allow
        return false
    }
}
