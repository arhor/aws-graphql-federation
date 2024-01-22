package com.github.arhor.aws.graphql.federation.tracing

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RequestIdPropagatingFilter : OrderedRequestContextFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, next: FilterChain) {
        super.doFilterInternal(req, res, withContextExtension(next))
    }

    private fun withContextExtension(next: FilterChain) = FilterChain { req, res ->
        if ((req is HttpServletRequest) && (res is HttpServletResponse)) {
            fun propagate(header: String, mdcProp: String) {
                val id = req.getHeader(header)?.takeIf(String::isNotEmpty)
                    ?: UUID.randomUUID().toString()

                MDC.put(mdcProp, id)
                res.setHeader(header, id)
            }
            propagate(TRACING_ID_HEADER, TRACING_ID_MDC_PROP)
            propagate(REQUEST_ID_HEADER, REQUEST_ID_MDC_PROP)
        }
        try {
            next.doFilter(req, res)
        } finally {
            MDC.remove(TRACING_ID_MDC_PROP)
            MDC.remove(REQUEST_ID_MDC_PROP)
        }
    }
}
