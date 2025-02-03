package com.github.arhor.aws.graphql.federation.starter.tracing

import com.github.arhor.aws.graphql.federation.common.constants.Attributes
import com.github.arhor.aws.graphql.federation.common.isNotNullOrBlank
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter
import org.springframework.stereotype.Component

@Component
class TracingPropagationRequestContextFilter : OrderedRequestContextFilter() {

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, next: FilterChain) {
        super.doFilterInternal(req, res, next.withContextExtension())
    }

    private fun FilterChain.withContextExtension() = FilterChain { req, res ->
        val addedAttrs = setupContextAttributes(req)
        try {
            doFilter(req, res)
        } finally {
            clearContextAttributes(req, addedAttrs)
        }
    }

    private fun setupContextAttributes(req: ServletRequest): List<String> {
        if (req !is HttpServletRequest) {
            return emptyList()
        }
        val addedAttributeKeys = ArrayList<String>(attributesSize)

        for (attribute in attributes) {
            val attributeKey = attribute.key
            val attributeVal = req.resolveAttribute(attribute)

            if (attributeVal != null) {
                MDC.put(attributeKey, attributeVal)
                req.setAttribute(attributeKey, attributeVal)

                addedAttributeKeys += attributeKey
            }
        }
        return addedAttributeKeys
    }

    private fun clearContextAttributes(req: ServletRequest, addedAttrs: List<String>) {
        for (attributeKey in addedAttrs) {
            MDC.remove(attributeKey)
            req.removeAttribute(attributeKey)
        }
    }

    private fun HttpServletRequest.resolveAttribute(attribute: Attributes): String? {
        return getHeader(attribute.key).takeIf { it.isNotNullOrBlank() }
            ?: attribute.defaultValue().takeIf { it.isNotNullOrBlank() }
    }

    companion object {
        private val attributes = listOf(Attributes.TRACE_ID, Attributes.REQUEST_ID)
        private val attributesSize = attributes.size
    }
}
