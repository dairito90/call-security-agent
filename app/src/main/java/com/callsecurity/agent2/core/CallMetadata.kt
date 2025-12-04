package com.callsecurity.agent2.core

data class CallMetadata(
    val number: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
