package com.callsecurity.agent2.core

class CallClassifier {

    data class ClassificationResult(
        val category: String,   // "spam", "unknown", "safe"
        val score: Int          // 0–100 risk score
    )

    fun classify(metadata: CallMetadata): ClassificationResult {

        val number = metadata.phoneNumber

        return when {
            number.startsWith("800") || number.startsWith("888") ->
                ClassificationResult("spam", 90)

            number.length < 10 ->
                ClassificationResult("unknown", 50)

            else ->
                ClassificationResult("safe", 5)
        }
    }
}
