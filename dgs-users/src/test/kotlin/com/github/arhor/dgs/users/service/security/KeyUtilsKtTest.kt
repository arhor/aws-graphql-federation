package com.github.arhor.dgs.users.service.security

import io.jsonwebtoken.SignatureAlgorithm.RS256
import io.jsonwebtoken.SignatureAlgorithm.RS384
import io.jsonwebtoken.SignatureAlgorithm.RS512
import io.jsonwebtoken.security.Keys
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith

internal class KeyUtilsKtTest : DescribeSpec({

    describe("composite conversion tests") {
        withData(
            nameFn = { "should convert $it public key to PEM string then convert it to the same public key" },
            RS256,
            RS384,
            RS512,
        ) {
            // Given
            val source = Keys.keyPairFor(it)

            // When
            val pemString = source.public.convertToPEMString()
            val publicKey = pemString.convertToRsaPublicKey()

            // Then
            pemString shouldStartWith PUBLIC_KEY_START
            pemString shouldEndWith PUBLIC_KEY_CLOSE
            publicKey shouldBeEqual source.public
        }
    }
})
