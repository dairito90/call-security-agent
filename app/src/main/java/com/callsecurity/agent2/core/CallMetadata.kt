package com.callsecurity.agent2.core

data class CallMetadata(
    val phoneNumber: String,
    val callDirection: String,   // incoming / outgoing
    val timestamp: Long
)
