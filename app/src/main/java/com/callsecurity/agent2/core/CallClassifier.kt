package com.callsecurity.agent2.core

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import java.security.MessageDigest
import java.util.regex.Pattern

/**
 * Multi-layer Call Classifier
 * Implements spam detection using pattern matching, threat database, and behavioral analysis
 */
class CallClassifier private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: CallClassifier? = null

        fun getInstance(context: Context): CallClassifier {
            return instance ?: synchronized(this) {
                instance ?: CallClassifier(context.applicationContext).also { instance = it }
            }
        }

        private const val TAG = "CallClassifier"
        
        // Aggressive mode thresholds
        private const val SPAM_HIGH_THRESHOLD = 90f
        private const val SPAM_LIKELY_THRESHOLD = 70f
        private const val SPAM_UNCERTAIN_THRESHOLD = 40f
    }

    private val patternDetector = PatternDetector()
    private val contactsCache = mutableSetOf<String>()

    init {
        loadContacts()
    }

    suspend fun classify(metadata: CallMetadata): ClassificationResult {
        val startTime = System.currentTimeMillis()

        // Quick whitelist check
        if (isWhitelisted(metadata.phoneNumber)) {
            return ClassificationResult(
                classification = Classification.WHITELISTED,
                confidence = 100f,
                action = Action.ALLOW,
                reasons = listOf("Number in contacts"),
                processingTime = System.currentTimeMillis() - startTime
            )
        }

        val reasons = mutableListOf<String>()

        // Layer 1: Pattern matching (25% weight)
        val patternScore = patternDetector.analyze(metadata.phoneNumber, reasons)

        // Layer 2: ML model (35% weight) - placeholder for now
        val mlScore = 50f  // TODO: Implement TensorFlow Lite model

        // Layer 3: Threat database (30% weight) - simplified for now
        val dbScore = 0f  // TODO: Implement database lookup

        // Layer 4: Behavioral analysis (10% weight)
        val behaviorScore = analyzeBehavior(metadata, reasons)

        // Calculate weighted final score
        val finalScore = (patternScore * 0.25f) +
                (mlScore * 0.35f) +
                (dbScore * 0.30f) +
                (behaviorScore * 0.10f)

        val classification = determineClassification(finalScore)
        val action = determineAction(classification, finalScore)

        val processingTime = System.currentTimeMillis() - startTime

        Log.d(TAG, "Classification: $classification, Score: $finalScore, Time: ${processingTime}ms")

        return ClassificationResult(
            classification = classification,
            confidence = finalScore,
            action = action,
            reasons = reasons.ifEmpty { listOf("Statistical analysis") },
            processingTime = processingTime
        )
    }

    private fun determineClassification(score: Float): Classification {
        return when {
            score >= SPAM_HIGH_THRESHOLD -> Classification.SPAM_HIGH
            score >= SPAM_LIKELY_THRESHOLD -> Classification.SPAM_LIKELY
            score >= SPAM_UNCERTAIN_THRESHOLD -> Classification.SPAM_UNCERTAIN
            else -> Classification.LEGITIMATE
        }
    }

    private fun determineAction(classification: Classification, score: Float): Action {
        // Aggressive blocking mode
        return when (classification) {
            Classification.WHITELISTED -> Action.ALLOW
            Classification.SPAM_HIGH -> Action.BLOCK
            Classification.SPAM_LIKELY -> Action.BLOCK  // Aggressive: block likely spam
            Classification.SPAM_UNCERTAIN -> Action.VOICEMAIL
            Classification.LEGITIMATE -> Action.ALLOW
        }
    }

    private fun isWhitelisted(phoneNumber: String): Boolean {
        val normalized = normalizeNumber(phoneNumber)
        return contactsCache.contains(normalized)
    }

    private fun loadContacts() {
        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                null, null, null
            )

            cursor?.use {
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (it.moveToNext()) {
                    val number = it.getString(numberIndex)
                    contactsCache.add(normalizeNumber(number))
                }
            }

            Log.d(TAG, "Loaded ${contactsCache.size} contacts")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading contacts", e)
        }
    }

    private fun analyzeBehavior(metadata: CallMetadata, reasons: MutableList<String>): Float {
        var score = 0f

        // Check if call is at odd hours
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        if (hour < 8 || hour > 21) {
            score += 15f
            reasons.add("Call at unusual hour")
        }

        return score
    }

    private fun normalizeNumber(phoneNumber: String?): String {
        return phoneNumber?.replace(Regex("[^\\d]"), "") ?: ""
    }

    private fun hashNumber(phoneNumber: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(phoneNumber.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}

/**
 * Pattern Detector - Identifies spam patterns in phone numbers
 */
class PatternDetector {
    
    private val sequentialPattern = Pattern.compile("(012|123|234|345|456|567|678|789|890)")
    private val repeatedDigitsPattern = Pattern.compile("(\\d)\\1{4,}")
    private val tollFreePattern = Pattern.compile("^1?(800|888|877|866|855|844|833)")
    private val premiumRatePattern = Pattern.compile("^1?900")

    fun analyze(phoneNumber: String, reasons: MutableList<String>): Float {
        var score = 0f
        val normalized = phoneNumber.replace(Regex("[^\\d]"), "")

        if (sequentialPattern.matcher(normalized).find()) {
            score += 30f
            reasons.add("Sequential number pattern")
        }

        if (repeatedDigitsPattern.matcher(normalized).find()) {
            score += 25f
            reasons.add("Repeated digits pattern")
        }

        if (tollFreePattern.matcher(normalized).find()) {
            score += 20f
            reasons.add("Toll-free number")
        }

        if (premiumRatePattern.matcher(normalized).find()) {
            score += 40f
            reasons.add("Premium rate number")
        }

        return score.coerceAtMost(100f)
    }
}

// Data classes
data class CallMetadata(
    val phoneNumber: String,
    val callerName: String?,
    val timestamp: Long,
    val presentation: Int
)

data class ClassificationResult(
    val classification: Classification,
    val confidence: Float,
    val action: Action,
    val reasons: List<String>,
    val processingTime: Long
) {
    val shouldBlock: Boolean
        get() = action == Action.BLOCK
}

enum class Classification {
    WHITELISTED,
    SPAM_HIGH,
    SPAM_LIKELY,
    SPAM_UNCERTAIN,
    LEGITIMATE
}

enum class Action {
    ALLOW,
    BLOCK,
    VOICEMAIL
}
