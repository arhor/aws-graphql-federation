@file:Suppress("SameParameterValue")

package com.github.arhor.aws.graphql.federation.users.service.security

import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

const val PUBLIC_KEY = "PUBLIC KEY"

val PUBLIC_KEY_START = startTag(PUBLIC_KEY)
val PUBLIC_KEY_CLOSE = closeTag(PUBLIC_KEY)

private val rsaKeyFactory by lazy { KeyFactory.getInstance("RSA") }

fun PublicKey.convertToPEMString() =
    """
    $PUBLIC_KEY_START
    ${Base64.getEncoder().encodeToString(encoded)}
    $PUBLIC_KEY_CLOSE
    """.trimIndent()

fun String.convertToRsaPublicKey() =
    this.removePrefix(PUBLIC_KEY_START)
        .removeSuffix(PUBLIC_KEY_CLOSE)
        .lineSequence()
        .joinToString(separator = "")
        .let { Base64.getDecoder().decode(it) }
        .let { X509EncodedKeySpec(it) }
        .let { rsaKeyFactory.generatePublic(it) as RSAPublicKey }

private fun tag(content: String) = "-----${content}-----"
private fun startTag(name: String) = tag("BEGIN ${name.uppercase()}")
private fun closeTag(name: String) = tag("END ${name.uppercase()}")
