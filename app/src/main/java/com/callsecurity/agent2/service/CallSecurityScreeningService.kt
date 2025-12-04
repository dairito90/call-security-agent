package com.callsecurity.agent2.service

import android.content.Intent
import android.telecom.Call
import android.telecom.CallScreeningService
import com.callsecurity.agent2.core.CallClassifier
import com.callsecurity.agent2.core.CallMetadata

class CallSecurityScreeningService : CallScreeningService() {

    private val classifier = CallClassifier()

    override fun onScreenCall(callDetails: Call.Details) {

        // Extract basic metadata about the incoming call
        val metadata = CallMetadata(
            phoneNumber = callDetails.handle.schemeSpecificPart ?: "",
            callDirection = if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING) {
                "incoming"
            } else {
                "outgoing"
            },
            timestamp = System.currentTimeMillis()
        )

        // Classify the call: spam / unknown / safe
        val result = classifier.classify(metadata)

        // Decide how Android should handle the call
        val response = when (result.category) {

            "spam" -> {
                CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(true)
                    .build()
            }

            "unknown" -> {
                CallResponse.Builder()
                    .setDisallowCall(false)
                    .setRejectCall(false)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
            }

            else -> {
                // safe call
                CallResponse.Builder()
                    .setDisallowCall(false)
                    .setRejectCall(false)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
            }
        }

        // Send the final response to Android
        respondToCall(callDetails, response)

        // Optional: broadcast classification result inside the app
        val intent = Intent("com.callsecurity.CALL_CLASSIFIED").apply {
            putExtra("phoneNumber", metadata.phone
