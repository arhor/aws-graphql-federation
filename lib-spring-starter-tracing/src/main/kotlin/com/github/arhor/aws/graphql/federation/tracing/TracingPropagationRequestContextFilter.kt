package com.github.arhor.aws.graphql.federation.tracing

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TracingPropagationRequestContextFilter : OrderedRequestContextFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, next: FilterChain) {
        super.doFilterInternal(req, res, next.withContextExtension())
    }

    private fun FilterChain.withContextExtension() = FilterChain { req, res ->
        if ((req is HttpServletRequest) && (res is HttpServletResponse)) {
            for (attribute in Attributes.entries) {
                val attributeKey = attribute.key
                val id = req.getHeader(attributeKey)?.takeIf(String::isNotEmpty)
                    ?: UUID.randomUUID().toString()

                MDC.put(attributeKey, id)
                res.setHeader(attributeKey, id)
                req.setAttribute(attributeKey, id)
            }
        }
        try {
            doFilter(req, res)
        } finally {
            for (attribute in Attributes.entries) {
                MDC.remove(attribute.key)
            }
        }
    }
}
