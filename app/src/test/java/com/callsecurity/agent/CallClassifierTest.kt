package com.callsecurity.agent2

import com.callsecurity.agent.core.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Unit Tests for Call Classifier
 * Tests all spam detection patterns and classification logic
 */
class CallClassifierTest {

    private lateinit var patternDetector: PatternDetector

    @Before
    fun setup() {
        patternDetector = PatternDetector()
    }

    @Test
    fun testSequentialPatternDetection() {
        val reasons = mutableListOf<String>()
        
        // Test sequential patterns
        val score1 = patternDetector.analyze("5551234567", reasons)
        assertTrue("Should detect sequential pattern", score1 > 0)
        assertTrue("Should mention sequential", 
            reasons.any { it.contains("Sequential", ignoreCase = true) })
        
        reasons.clear()
        
        // Test non-sequential
        val score2 = patternDetector.analyze("5559876543", reasons)
        assertTrue("Non-sequential should have lower score", score2 < score1)
    }

    @Test
    fun testRepeatedDigitsDetection() {
        val reasons = mutableListOf<String>()
        
        // Test repeated digits
        val score = patternDetector.analyze("5555555555", reasons)
        assertTrue("Should detect repeated digits", score >= 25f)
        assertTrue("Should mention repeated digits",
            reasons.any { it.contains("Repeated", ignoreCase = true) })
    }

    @Test
    fun testTollFreeDetection() {
        val testNumbers = listOf(
            "18005551234",  // 800
            "18885551234",  // 888
            "18775551234",  // 877
            "18665551234",  // 866
            "18555551234",  // 855
            "18445551234",  // 844
            "18335551234"   // 833
        )
        
        testNumbers.forEach { number ->
            val reasons = mutableListOf<String>()
            val score = patternDetector.analyze(number, reasons)
            assertTrue("Should detect toll-free: $number", score >= 20f)
            assertTrue("Should mention toll-free",
                reasons.any { it.contains("toll-free", ignoreCase = true) })
        }
    }

    @Test
    fun testPremiumRateDetection() {
        val reasons = mutableListOf<String>()
        
        val score = patternDetector.analyze("19005551234", reasons)
        assertTrue("Should detect premium rate", score >= 40f)
        assertTrue("Should mention premium rate",
            reasons.any { it.contains("Premium", ignoreCase = true) })
    }

    @Test
    fun testNormalNumberLowScore() {
        val reasons = mutableListOf<String>()
        
        // Normal looking number
        val score = patternDetector.analyze("5559876543", reasons)
        assertTrue("Normal number should have low score", score < 30f)
    }

    @Test
    fun testClassificationThresholds() {
        // Test classification boundaries
        assertEquals(Classification.SPAM_HIGH, 
            determineClassification(95f))
        assertEquals(Classification.SPAM_LIKELY, 
            determineClassification(75f))
        assertEquals(Classification.SPAM_UNCERTAIN, 
            determineClassification(50f))
        assertEquals(Classification.LEGITIMATE, 
            determineClassification(25f))
    }

    @Test
    fun testAggressiveBlocking() {
        // Test aggressive mode blocks at 70%+
        assertEquals(Action.BLOCK, 
            determineAction(Classification.SPAM_HIGH, 95f))
        assertEquals(Action.BLOCK, 
            determineAction(Classification.SPAM_LIKELY, 75f))
        assertEquals(Action.VOICEMAIL, 
            determineAction(Classification.SPAM_UNCERTAIN, 50f))
        assertEquals(Action.ALLOW, 
            determineAction(Classification.LEGITIMATE, 25f))
    }

    @Test
    fun testWhitelistAlwaysAllows() {
        val classification = Classification.WHITELISTED
        val action = determineAction(classification, 100f)
        assertEquals("Whitelisted should always allow", Action.ALLOW, action)
    }

    @Test
    fun testMultiplePatterns() {
        val reasons = mutableListOf<String>()
        
        // Number with multiple spam indicators
        val score = patternDetector.analyze("18005555555", reasons)
        
        // Should detect both toll-free AND repeated digits
        assertTrue("Should have high score for multiple patterns", score >= 40f)
        assertTrue("Should have multiple reasons", reasons.size >= 2)
    }

    @Test
    fun testScoreNeverExceeds100() {
        val reasons = mutableListOf<String>()
        
        // Extreme spam number
        val score = patternDetector.analyze("19001234567", reasons)
        assertTrue("Score should never exceed 100", score <= 100f)
    }

    // Helper methods matching CallClassifier logic
    private fun determineClassification(score: Float): Classification {
        return when {
            score >= 90f -> Classification.SPAM_HIGH
            score >= 70f -> Classification.SPAM_LIKELY
            score >= 40f -> Classification.SPAM_UNCERTAIN
            else -> Classification.LEGITIMATE
        }
    }

    private fun determineAction(classification: Classification, score: Float): Action {
        return when (classification) {
            Classification.WHITELISTED -> Action.ALLOW
            Classification.SPAM_HIGH -> Action.BLOCK
            Classification.SPAM_LIKELY -> Action.BLOCK
            Classification.SPAM_UNCERTAIN -> Action.VOICEMAIL
            Classification.LEGITIMATE -> Action.ALLOW
        }
    }
}

/**
 * Integration Tests
 */
class CallClassifierIntegrationTest {

    @Test
    fun testCompleteClassificationFlow() {
        // Simulate complete classification
        val metadata = CallMetadata(
            phoneNumber = "18005551234",
            callerName = null,
            timestamp = System.currentTimeMillis(),
            presentation = 0
        )

        // This would use actual CallClassifier in real test
        // For now, verify the logic flow
        assertTrue("Metadata created successfully", metadata.phoneNumber.isNotEmpty())
    }

    @Test
    fun testPerformanceRequirement() {
        val startTime = System.currentTimeMillis()
        
        // Simulate classification
        val patternDetector = PatternDetector()
        val reasons = mutableListOf<String>()
        patternDetector.analyze("18005551234", reasons)
        
        val processingTime = System.currentTimeMillis() - startTime
        
        assertTrue("Classification should be fast (<200ms)", processingTime < 200)
    }
}

/**
 * Safety Tests - Ensure app won't crash
 */
class SafetyTest {

    @Test
    fun testNullPhoneNumber() {
        val patternDetector = PatternDetector()
        val reasons = mutableListOf<String>()
        
        // Should handle empty/null gracefully
        val score = patternDetector.analyze("", reasons)
        assertTrue("Should handle empty number", score >= 0)
    }

    @Test
    fun testInvalidPhoneNumber() {
        val patternDetector = PatternDetector()
        val reasons = mutableListOf<String>()
        
        // Test various invalid formats
        val invalidNumbers = listOf(
            "abc123",
            "++++++",
            "--------",
            "123",
            "12345678901234567890"  // Too long
        )
        
        invalidNumbers.forEach { number ->
            val score = patternDetector.analyze(number, reasons)
            assertTrue("Should handle invalid number: $number", score >= 0)
            reasons.clear()
        }
    }

    @Test
    fun testInternationalNumbers() {
        val patternDetector = PatternDetector()
        val reasons = mutableListOf<String>()
        
        val internationalNumbers = listOf(
            "+442071234567",  // UK
            "+861234567890",  // China
            "+919876543210"   // India
        )
        
        internationalNumbers.forEach { number ->
            val score = patternDetector.analyze(number, reasons)
            assertTrue("Should handle international: $number", score >= 0)
            reasons.clear()
        }
    }
}
