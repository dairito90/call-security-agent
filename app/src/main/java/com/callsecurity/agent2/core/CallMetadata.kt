package com.callsecurity.agent2.core

/**
 * Metadata gathered for each incoming or outgoing call.
 */
data class CallMetadata(
    val phoneNumber: String,
    val timestamp: Long,
    val callerName: String? = null,
    val presentation: Int = 0
)
