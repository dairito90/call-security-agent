package com.callsecurity.agent2.core

/**
 * Simple classification wrapper around SpamEngine.
 * 
 * You can expand this later for ML scoring, cloud lookups, etc.
 */
object CallClassifier {

    enum class Result {
        LEGITIMATE,
        SPAM
    }

    /**
     * Classifies a call based on metadata using the SpamEngine rules.
     */
    fun classify(metadata: CallMetadata): Result {
        return if (SpamEngine.isSpam(metadata)) {
            Result.SPAM
        } else {
            Result.LEGITIMATE
        }
    }
}
