package com.github.arhor.dgs.users.service

interface TokenProvider {

    fun createSignedJwt(identity: String, params: Map<String, Any>): String
    fun activePublicKey(): String
}
