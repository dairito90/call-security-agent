package com.callsecurity.agent2.service

import android.content.Intent
import android.telecom.Call
import android.telecom.CallScreeningService
import com.callsecurity.agent2.core.CallClassifier
import com.callsecurity.agent2.core.CallMetadata

class CallSecurityScreeningService : CallScreeningService() {

    private val classifier = CallClassifier()

    override fun onScreenCall(callDetails: Call.Details) {

        // Extract metadata
        val metadata = CallMetadata(
            phoneNumber = callDetails.handle?.schemeSpecificPart ?: "",
            callDirection = if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING)
                "incoming"
            else "outgoing",
            timestamp = System.currentTimeMillis()
        )

        // Run classifier
        val result = classifier.classify(metadata)

        // Map category → action
        val response = when (result.category) {

            // BLOCK spam
            "spam" -> CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(true)
                .setSkipNotification(true)
                .build()

            // SCREEN unknown callers
            "unknown" -> CallResponse.Builder()
                .setSilenceCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()

            // ALLOW safe callers
            else -> CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .build()
        }

        respondToCall(callDetails, response)

        // Optional: notify app UI about classification
        sendBroadcast(
            Intent("CALL_SECURITY_EVENT").apply {
                putExtra("number", metadata.phoneNumber)
                putExtra("category", result.category)
                putExtra("score", result.score)
            }
        )
    }
}
