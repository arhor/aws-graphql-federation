package com.github.arhor.dgs.users.service.security

import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

const val PUBLIC_KEY_START_TAG = "-----BEGIN PUBLIC KEY-----"
const val PUBLIC_KEY_END_TAG = "-----END PUBLIC KEY-----"

fun PublicKey.toPemString(): String = """
    $PUBLIC_KEY_START_TAG
    ${Base64.getEncoder().encodeToString(encoded)}
    $PUBLIC_KEY_END_TAG
    """.trimIndent()

fun String.toRsaPublicKey(): PublicKey {
    val encoded =
        this.removePrefix(PUBLIC_KEY_START_TAG)
            .removeSuffix(PUBLIC_KEY_END_TAG)
            .lineSequence()
            .joinToString(separator = "")
            .let { Base64.getDecoder().decode(it) }

    val keyFactory = KeyFactory.getInstance("RSA")
    val keySpec = X509EncodedKeySpec(encoded)

    return keyFactory.generatePublic(keySpec)
}
