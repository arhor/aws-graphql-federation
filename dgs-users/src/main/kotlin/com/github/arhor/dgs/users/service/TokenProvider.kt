package com.github.arhor.dgs.users.service

import io.jsonwebtoken.JwtBuilder

interface TokenProvider {

    fun createSignedJwt(customize: JwtBuilder.() -> Unit = {}): String
    fun activePublicKey(): String
}
