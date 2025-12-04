package com.callsecurity.agent2.core

class CallClassifier {

    data class Result(
        val category: String,   // "spam", "unknown", "safe"
        val score: Int          // scoring model placeholder
    )

    fun classify(metadata: CallMetadata): Result {
        val number = metadata.phoneNumber

        // Simple demo logic — replace with AI model later
        return when {
            number.isEmpty() -> Result("unknown", 40)
            number.startsWith("800") -> Result("spam", 95)
            number.length < 10 -> Result("unknown", 50)
            else -> Result("safe", 10)
        }
    }
}
