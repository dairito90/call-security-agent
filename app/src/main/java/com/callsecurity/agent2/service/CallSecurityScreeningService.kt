package com.callsecurity.agent2.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Call.Details
import android.util.Log
import com.callsecurity.agent2.core.SpamEngine

class CallShieldScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Details) {
        try {
            val phoneNumber = callDetails.handle.schemeSpecificPart ?: ""
            Log.d("CallShield", "Incoming call: $phoneNumber")

            val isSpam = SpamEngine.isSpamNumber(phoneNumber)

            val response = CallResponse.Builder()
                .setDisallowCall(isSpam)
                .setRejectCall(isSpam)
                .setSilenceCall(isSpam)
                .setSkipCallLog(isSpam)
                .setSkipNotification(isSpam)
                .build()

            respondToCall(callDetails, response)

        } catch (e: Exception) {
            Log.e("CallShield", "CallScreeningService error", e)
        }
    }
}
