package com.callsecurity.agent2.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.callsecurity.agent.core.CallClassifier
import com.callsecurity.agent.core.CallMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Call Screening Service - Main entry point for incoming call interception
 * This service is called by Android system for every incoming call
 */
class CallSecurityScreeningService : CallScreeningService() {

    private val TAG = "CallSecurityService"
    private lateinit var classifier: CallClassifier

    override fun onCreate() {
        super.onCreate()
        classifier = CallClassifier.getInstance(this)
        Log.i(TAG, "Call Security Screening Service started")
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: ""
        val callerName = callDetails.callerDisplayName
        
        Log.i(TAG, "Screening call from: $phoneNumber")

        // Process call asynchronously to avoid blocking
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = classifyCall(phoneNumber, callDetails)
                
                // Build response based on classification
                val response = buildCallResponse(result.shouldBlock, result.confidence)
                
                // Respond to the call
                withContext(Dispatchers.Main) {
                    respondToCall(callDetails, response)
                }
                
                Log.i(TAG, "Call from $phoneNumber: ${result.classification} " +
                        "(${result.confidence}%) -> ${if (result.shouldBlock) "BLOCKED" else "ALLOWED"}")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error screening call", e)
                // On error, allow call to be safe
                val safeResponse = CallResponse.Builder()
                    .setDisallowCall(false)
                    .setRejectCall(false)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
                
                withContext(Dispatchers.Main) {
                    respondToCall(callDetails, safeResponse)
                }
            }
        }
    }

    private suspend fun classifyCall(
        phoneNumber: String,
        callDetails: Call.Details
    ): com.callsecurity.agent.core.ClassificationResult {
        val metadata = CallMetadata(
            phoneNumber = phoneNumber,
            callerName = callDetails.callerDisplayName,
            timestamp = System.currentTimeMillis(),
            presentation = callDetails.callerNumberVerificationStatus
        )

        return classifier.classify(metadata)
    }

    private fun buildCallResponse(shouldBlock: Boolean, confidence: Float): CallResponse {
        return if (shouldBlock) {
            // Block the call - aggressive mode
            CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(false)  // Keep in log for audit
                .setSkipNotification(true)  // Silent block
                .build()
        } else {
            // Allow the call
            CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()
        }
    }
}
