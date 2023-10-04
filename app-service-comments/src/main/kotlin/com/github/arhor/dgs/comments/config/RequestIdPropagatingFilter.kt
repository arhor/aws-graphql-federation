package com.github.arhor.dgs.comments.config

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

    private fun withContextExtension(filterChain: FilterChain) = FilterChain { req, res ->
        if ((req is HttpServletRequest) && (res is HttpServletResponse)) {
            val requestId = req.getHeader(REQUEST_ID_HEADER)?.takeIf(String::isNotEmpty)
                ?: UUID.randomUUID().toString()

            MDC.put(REQUEST_ID_MDC_PROP, requestId)
            res.addHeader(REQUEST_ID_HEADER, requestId)
        }
        try {
            filterChain.doFilter(req, res)
        } finally {
            MDC.clear()
        }
    }

    companion object {
        private const val REQUEST_ID_HEADER = "X-Request-ID"
        private const val REQUEST_ID_MDC_PROP = "request-id"
    }
}
