package com.github.arhor.dgs.lib

import org.slf4j.MDC
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class ExtendedRequestContextFilter : OrderedRequestContextFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, next: FilterChain) {
        super.doFilterInternal(req, res, next.withContextExtension())
    }

    private fun FilterChain.withContextExtension() = FilterChain { req, res ->
        if ((req is HttpServletRequest) && (res is HttpServletResponse)) {
            val requestId = Optional.ofNullable(req.getHeader(REQUEST_ID))
                .map(UUID::fromString)
                .orElseGet(UUID::randomUUID)
                .also { CurrentRequestContext(it) }
                .toString()

            MDC.put("request-id", requestId)
            res.addHeader(REQUEST_ID, requestId)
        }
        try {
            doFilter(req, res)
        } finally {
            MDC.clear()
        }
    }

    companion object {
        private const val REQUEST_ID = "X-REQUEST-ID"
    }
}
