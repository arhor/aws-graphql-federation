package com.github.arhor.aws.graphql.federation.users.service.security

import com.github.arhor.aws.graphql.federation.users.service.security.PUBLIC_KEY_CLOSE
import com.github.arhor.aws.graphql.federation.users.service.security.PUBLIC_KEY_START
import com.github.arhor.aws.graphql.federation.users.service.security.convertToPEMString
import com.github.arhor.aws.graphql.federation.users.service.security.convertToRsaPublicKey
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureAlgorithm.RS256
import io.jsonwebtoken.SignatureAlgorithm.RS384
import io.jsonwebtoken.SignatureAlgorithm.RS512
import io.jsonwebtoken.security.Keys
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class KeyUtilsKtTest {

    @MethodSource
    @ParameterizedTest
    fun `should convert public key to PEM string then convert it to the same public key`(algorithm: SignatureAlgorithm) {
        // given
        val source = Keys.keyPairFor(algorithm)

        // when
        val pemString = source.public.convertToPEMString()
        val publicKey = pemString.convertToRsaPublicKey()

        // then
        assertThat(pemString)
            .isNotNull()
            .startsWith(PUBLIC_KEY_START)
            .endsWith(PUBLIC_KEY_CLOSE)

        assertThat(publicKey)
            .isNotNull()
            .isEqualTo(source.public)
    }

    companion object {
        @JvmStatic
        fun `should convert public key to PEM string then convert it to the same public key`(): Stream<Arguments> = Stream.of(
            arguments(RS256),
            arguments(RS384),
            arguments(RS512),
        )
    }
}
