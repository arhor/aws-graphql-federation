package com.github.arhor.aws.graphql.federation.users.service.impl

import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.service.TokenProvider
import com.github.arhor.aws.graphql.federation.users.service.security.convertToPEMString
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import kotlin.time.Duration

@Trace
@Component
class JwtTokenProviderImpl(@Value("\${app-props.jwt.expire:#{null}}") expire: String) : TokenProvider {

    private val jwtExpiration = Duration.parse(expire).inWholeMilliseconds
    private val jwtSigningKey = Keys.keyPairFor(SignatureAlgorithm.RS512)

    override fun createSignedJwt(customize: JwtBuilder.() -> Unit): String {
        val dateFrom = System.currentTimeMillis()
        val dateTill = dateFrom + jwtExpiration

        return Jwts.builder()
            .apply(customize)
            .setIssuedAt(Date(dateFrom))
            .setExpiration(Date(dateTill))
            .signWith(jwtSigningKey.private)
            .compact()
    }

    override fun activePublicKey(): String {
        return jwtSigningKey.public.convertToPEMString()
    }
}