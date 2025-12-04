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
            phoneNumber = call
