package com.callsecurity.agent2.service

import android.telecom.CallScreeningService
import com.callsecurity.agent2.core.CallClassifier

class CallSecurityScreeningService : CallScreeningService() {

    private val classifier = CallClassifier()

    override fun onScreenCall(details: Call.Details) {
        val number = details.handle.schemeSpecificPart

        val result = CallResponse.Builder()
        if (classifier.isSpam(number)) {
            result.setRejectCall(true)
            result.setSilenceCall(true)
            result.setSkipCallLog(true)
        }
        respondToCall(details, result.build())
    }
}
