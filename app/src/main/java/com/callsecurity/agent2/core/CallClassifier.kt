package com.callsecurity.agent2.core

class CallClassifier {

    data class Result(
        val category: String,  // spam / safe / unknown
        val score: Int
    )

    fun classify(metadata: CallMetadata): Result {
        val score = SpamEngine.getSpamScore(metadata.phoneNumber)

        val category = when {
            score >= 60 -> "spam"
            score in 30..59 -> "unknown"
            else -> "safe"
        }

        return Result(category, score)
    }
}
