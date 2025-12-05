package com.callsecurity.agent2.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.callsecurity.agent2.core.CallClassifier
import com.callsecurity.agent2.core.CallMetadata

class CallSecurityScreeningService : CallScreeningService() {

    private val classifier = CallClassifier()

    override fun onScreenCall(callDetails: Call.Details) {
        try {
            val number = callDetails.handle?.schemeSpecificPart ?: ""
            val direction =
                if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING) "incoming"
                else "outgoing"

            val metadata = CallMetadata(number, direction, System.currentTimeMillis())
            val result = classifier.classify(metadata)

            Log.d("CallShield", "Incoming call: $number score=${result.score} type=${result.category}")

            val response = CallResponse.Builder()
                .setDisallowCall(result.category == "spam")
                .setRejectCall(result.category == "spam")
                .setSilenceCall(result.category == "spam")
                .setSkipCallLog(result.category == "spam")
                .setSkipNotification(result.category == "spam")
                .build()

            respondToCall(callDetails, response)

        } catch (e: Exception) {
            Log.e("CallShield", "Error screening call", e)
        }
    }
}
