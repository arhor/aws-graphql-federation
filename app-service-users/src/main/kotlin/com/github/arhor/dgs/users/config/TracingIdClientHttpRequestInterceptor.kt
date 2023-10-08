package com.github.arhor.dgs.users.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.client.RestTemplateRequestCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequest
import org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST
import org.springframework.web.context.request.RequestContextHolder

@Configuration(proxyBeanMethods = false)
class TracingIdClientHttpRequestInterceptor {

    @Bean
    fun restTemplateRequestCustomizer() = RestTemplateRequestCustomizer<ClientHttpRequest> {
        val attributes = RequestContextHolder.currentRequestAttributes()
        val request = attributes.resolveReference(REFERENCE_REQUEST) as HttpServletRequest

        fun propagate(header: String) {
            it.headers[header] = request.getHeader(header)
        }
        propagate(TRACING_ID_HEADER)
        propagate(REQUEST_ID_HEADER)
    }

    companion object {
        private const val TRACING_ID_HEADER = "X-Tracing-ID"
        private const val REQUEST_ID_HEADER = "X-Request-ID"
    }
}
