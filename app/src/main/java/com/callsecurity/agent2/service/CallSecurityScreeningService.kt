package com.callsecurity.agent2.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.callsecurity.agent2.core.CallClassifier
// NOTE: Assuming you have access to CoroutineScope and Dispatchers
import kotlinx.coroutines.* // 1. Service needs a CoroutineScope to launch non-blocking tasks
class CallSecurityScreeningService : CallScreeningService(), CoroutineScope by MainScope() {

    // 2. The CallClassifier should be provided, not instantiated here.
    // For simplicity, we'll keep the instantiation, but note the architectural flaw (Issue #3).
    private val classifier = CallClassifier() 

    override fun onScreenCall(callDetails: Call.Details) {
        // CRITICAL FIX: Launch the heavy work on a background thread (IO Dispatcher)
        launch { 
            try {
                // 3. Switch to the IO (Background) thread for the blocking call
                val result = withContext(Dispatchers.IO) { 
                    val number = callDetails.handle?.schemeSpecificPart ?: ""
                    val direction = 
                        if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING) "incoming"
                        else "outgoing"

                    val metadata = CallMetadata(number, direction, System.currentTimeMillis())
                    
                    // This is the potentially BLOCKING CALL that is now safe
                    classifier.classify(metadata)
                } 

                // Execution returns to the Main Thread here

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
                // HIGH Production Fix (Issue #2): Integrate a proper crash reporter here
                // Example: Crashlytics.recordException(e)
                Log.e("CallShield", "Error screening call", e)
            }
        }
    }
    
    // Clean up the CoroutineScope when the service is destroyed
    override fun onDestroy() {
        super.onDestroy()
        cancel() // Cancels all running coroutines in this scope
    }
}

