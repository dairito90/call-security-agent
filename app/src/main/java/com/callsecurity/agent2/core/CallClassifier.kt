package com.callsecurity.agent2.core

data class ClassificationResult(
    val category: String,
    val score: Int
)

class CallClassifier {

    private val spamPatterns = listOf(
        "800",
        "888",
        "877",
        "900"
    )

    fun classify(metadata: CallMetadata): ClassificationResult {

        val number = metadata.phoneNumber

        if (number.isBlank()) {
            return ClassificationResult("unknown", 0)
        }

        // Check for spam pattern
        val isSpam = spamPatterns.any { pattern ->
            number.contains(pattern)
        }

        return when {
            isSpam -> ClassificationResult("spam", 90)
            number.length <= 5 -> ClassificationResult("unknown", 50)
            else -> ClassificationResult("safe", 10)
        }
    }
}
