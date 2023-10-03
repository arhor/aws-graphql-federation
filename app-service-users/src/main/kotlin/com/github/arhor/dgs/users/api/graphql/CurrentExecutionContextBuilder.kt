package com.github.arhor.dgs.users.api.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.netflix.graphql.dgs.context.DgsCustomContextBuilderWithRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest

@Component
class CurrentExecutionContextBuilder(
    private val objectMapper: ObjectMapper,
) : DgsCustomContextBuilderWithRequest<CurrentExecutionContext> {

    override fun build(
        extensions: Map<String, Any>?,
        headers: HttpHeaders?,
        webRequest: WebRequest?,
    ): CurrentExecutionContext {
        val currentUser =
            headers
                ?.let { it[HEADER_CURRENT_USER] }
                ?.singleOrNull()
                ?.let { objectMapper.readValue<CurrentUser>(it) }

        return CurrentExecutionContext(
            currentUser = currentUser,
        )
    }

    companion object {
        private const val HEADER_CURRENT_USER = "x-current-user"
    }
}
