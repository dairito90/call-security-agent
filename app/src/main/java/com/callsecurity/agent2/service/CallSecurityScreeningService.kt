package com.callsecurity.agent2.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Call.Details
import android.util.Log
import com.callsecurity.agent2.core.CallClassifier

class CallSecurityScreeningService : CallScreeningService() {

    private val classifier = CallClassifier()

    override fun onScreenCall(callDetails: Details) {
        try {
            val phoneNumber = callDetails.handle.schemeSpecificPart ?: ""
            Log.d("CallShield", "Incoming call: $phoneNumber")

            val isSpam = classifier.isSpam(phoneNumber)
            val score = classifier.classificationScore(phoneNumber)

            Log.d("CallShield", "Spam: $isSpam (Score $score)")

            val response = CallResponse.Builder()
                .setDisallowCall(isSpam)
                .setRejectCall(isSpam)
                .setSilenceCall(isSpam)
                .setSkipCallLog(isSpam)
                .setSkipNotification(isSpam)
                .build()

            respondToCall(callDetails, response)

        } catch (e: Exception) {
            Log.e("CallShield", "Error during screening: ${e.message}")
        }
    }
}
