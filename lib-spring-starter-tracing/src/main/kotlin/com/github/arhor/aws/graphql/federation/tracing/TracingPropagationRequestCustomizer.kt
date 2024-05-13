package com.github.arhor.aws.graphql.federation.tracing

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.client.RestTemplateRequestCustomizer
import org.springframework.http.client.ClientHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST
import org.springframework.web.context.request.RequestContextHolder

@Component
class TracingPropagationRequestCustomizer : RestTemplateRequestCustomizer<ClientHttpRequest> {

    override fun customize(clientRequest: ClientHttpRequest) {
        val attributeKey = TRACING_ID_KEY
        val attributes = RequestContextHolder.currentRequestAttributes()
        val serverRequest = attributes.resolveReference(REFERENCE_REQUEST) as HttpServletRequest

        clientRequest.headers[attributeKey] = serverRequest.getHeader(attributeKey)
    }
}
