package com.callsecurity.agent2.service

import android.content.Intent
import android.telecom.Call
import android.telecom.CallScreeningService
import com.callsecurity.agent2.core.CallClassifier
import com.callsecurity.agent2.core.CallMetadata

class CallSecurityScreeningService : CallScreeningService() {

    private val classifier = CallClassifier()

    override fun onScreenCall(callDetails: Call.Details) {

        val metadata = CallMetadata(
            phoneNumber = callDetails.handle?.schemeSpecificPart ?: "",
            callDirection = if (callDetails.callDirection ==
                Call.Details.DIRECTION_INCOMING
            ) "incoming" else "outgoing",
            timestamp = System.currentTimeMillis()
        )

        val result = classifier.classify(metadata)

        val response = when (result.category) {

            "spam" -> CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(true)
                .setSkipNotification(true)
                .build()

            "unknown" -> CallResponse.Builder()
                .setSilenceCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()

            else -> CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .build()
        }

        respondToCall(callDetails, response)

        sendBroadcast(
            Intent("CALL_SECURITY_EVENT").apply {
                putExtra("number", metadata.phoneNumber)
                putExtra("category", result.category)
                putExtra("score", result.score)
            }
        )
    }
}
