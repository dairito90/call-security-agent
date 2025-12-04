package com.callsecurity.agent2.core

object SpamEngine {

    private val blacklist = listOf(
        "8001234567",
        "8889990000",
        "8772049099",
        "9001112233"
    )

    fun isSpam(number: String): Boolean {
        if (number.isBlank()) return false
        return blacklist.any { number.contains(it) }
    }
}
