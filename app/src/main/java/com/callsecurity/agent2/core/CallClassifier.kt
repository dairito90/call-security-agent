package com.callsecurity.agent2.core

class CallClassifier {

    fun isSpam(phone: String): Boolean {
        val score = SpamEngine.scoreNumber(phone)
        return score >= 50
    }

    fun classificationScore(phone: String): Int {
        return SpamEngine.scoreNumber(phone)
    }
}
