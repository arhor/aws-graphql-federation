package com.github.arhor.aws.graphql.federation.posts.api.routes

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.servlet.function.ServerRequest
import java.net.URI

class MainRouterTest {

    private val mainRouter = MainRouter()
    private val messageConverters = listOf(MappingJackson2HttpMessageConverter())

    @Test
    fun `should return empty response with NO_CONTENT status trying to get favicon`() {
        // given
        val requestURI = URI.create("/favicon.ico")
        val httpMethod = HttpMethod.GET
        val httpStatus = HttpStatus.NO_CONTENT

        val request = createMockServerRequest(requestURI, httpMethod)

        // When
        val response = mainRouter.route(request).map { it.handle(request) }

        // then
        assertThat(response)
            .isNotNull
            .isNotEmpty
            .get()
            .returns(httpStatus, Assertions.from { it.statusCode() })
    }

    private fun createMockServerRequest(uri: URI, method: HttpMethod, content: String? = null): ServerRequest =
        ServerRequest.create(
            MockHttpServletRequest(method.name(), uri.toString())
                .also { it.setContent(content?.encodeToByteArray()) }
                .also { it.contentType = "application/json" },
            messageConverters,
        )
}
