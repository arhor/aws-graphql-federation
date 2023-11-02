package com.github.arhor.aws.graphql.federation.users.api.router

import com.github.arhor.aws.graphql.federation.users.api.routes.MainRouter
import com.github.arhor.aws.graphql.federation.users.service.UserService
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.servlet.function.ServerRequest
import java.net.URI

internal class MainRouterTest {

    private val userService = mockk<UserService>()

    private val mainRouter = MainRouter(
        userService,
    )

    @Test
    fun `should return empty response with NO_CONTENT status trying to get favicon`() {
        // Given
        val requestURI = URI.create("/favicon.ico")
        val httpMethod = HttpMethod.GET
        val httpStatus = HttpStatus.NO_CONTENT

        val request = createMockServerRequest(httpMethod, requestURI)

        // When
        val response = mainRouter.route(request).map { it.handle(request) }

        // Then
        assertThat(response)
            .isNotNull
            .isNotEmpty
            .get()
            .returns(httpStatus, from { it.statusCode() })
    }

    private fun createMockServerRequest(httpMethod: HttpMethod, requestURI: URI): ServerRequest {
        val servletRequest = MockHttpServletRequest(httpMethod.name(), requestURI.toString())
        val messageReaders = emptyList<HttpMessageConverter<*>>()

        return ServerRequest.create(servletRequest, messageReaders)
    }
}
