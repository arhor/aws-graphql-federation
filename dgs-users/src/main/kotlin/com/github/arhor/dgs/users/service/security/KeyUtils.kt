@file:Suppress("SameParameterValue")

package com.github.arhor.dgs.users.service.security

import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

const val PUBLIC_KEY = "PUBLIC KEY"
val PUBLIC_KEY_START = tagStart(PUBLIC_KEY)
val PUBLIC_KEY_CLOSE = tagClose(PUBLIC_KEY)

fun PublicKey.toPemString(): String = """
    $PUBLIC_KEY_START
    ${Base64.getEncoder().encodeToString(encoded)}
    $PUBLIC_KEY_CLOSE
    """.trimIndent()

fun String.toRsaPublicKey(): PublicKey {
    val encoded =
        this.removePrefix(PUBLIC_KEY_START)
            .removeSuffix(PUBLIC_KEY_CLOSE)
            .lineSequence()
            .joinToString(separator = "")
            .let { Base64.getDecoder().decode(it) }

    val keyFactory = KeyFactory.getInstance("RSA")
    val keySpec = X509EncodedKeySpec(encoded)

    return keyFactory.generatePublic(keySpec)
}

private fun tag(content: String) = "-----${content}-----"
private fun tagStart(name: String) = tag("BEGIN ${name.uppercase()}")
private fun tagClose(name: String) = tag("END ${name.uppercase()}")
