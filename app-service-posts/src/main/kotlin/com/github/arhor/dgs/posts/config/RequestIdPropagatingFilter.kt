package com.github.arhor.dgs.posts.config

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
            MDC.clear()
        }
    }

    companion object {
        private const val TRACING_ID_HEADER = "X-Tracing-ID"
        private const val REQUEST_ID_HEADER = "X-Request-ID"
        private const val TRACING_ID_MDC_PROP = "tracing-id"
        private const val REQUEST_ID_MDC_PROP = "request-id"
    }
}
