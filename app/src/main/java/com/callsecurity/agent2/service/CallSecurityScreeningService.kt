package com.callsecurity.agent2.service

import android.content.Intent
import android.telecom.Call
import android.telecom.CallScreeningService
import com.callsecurity.agent2.core.CallClassifier
import com.callsecurity.agent2.core.CallMetadata

class CallSecurityScreeningService : CallScreeningService() {

    private val classifier = CallClassifier()

    override fun onScreenCall(callDetails: Call.Details) {

        // Extract caller phone number
        val number = callDetails.handle?.schemeSpecificPart ?: ""

        // Build metadata object for classifier
        val metadata = CallMetadata(
            phoneNumber = number,
            timestamp = System.currentTimeMillis(),
            callerName = null,             // Optional, classifier supports null
            presentation = callDetails.presentation
        )

        // Run classifier — returns category + score
        val result = classifier.classify(metadata)

        // Map classifier result → Android call action
        val response = when (result.category.lowercase()) {

            // 🚫 BLOCK spam calls
            "spam" -> CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(true)
                .setSkipNotification(true)
                .build()

            // 🤫 Silence unknown callers
            "unknown" -> CallResponse.Builder()
                .setSilenceCall(true)
                .setDisallowCall(false)
                .setRejectCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()

            // ✔️ Allow safe / known callers
            else -> CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .build()
        }

        // Pass instructions to the system
        respondToCall(callDetails, response)

        // Notify app UI of classification event (optional)
        sendBroadcast(
            Intent("CALL_SECURITY_EVENT").apply {
                putExtra("number", number)
                putExtra("category", result.category)
                putExtra("score", result.score)
            }
        )
    }
}
