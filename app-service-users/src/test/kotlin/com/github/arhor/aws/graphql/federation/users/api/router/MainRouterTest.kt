package com.github.arhor.aws.graphql.federation.users.api.router

import com.github.arhor.aws.graphql.federation.security.CurrentUser
import com.github.arhor.aws.graphql.federation.users.api.routes.MainRouter
import com.github.arhor.aws.graphql.federation.users.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.servlet.function.ServerRequest
import java.net.URI

internal class MainRouterTest {

    private val userService = mockk<UserService>()
    private val mainRouter = MainRouter(
        userService,
    )
    private val messageConverters = listOf(
        MappingJackson2HttpMessageConverter(),
    )

    @Test
    fun `should return empty response with NO_CONTENT status trying to get favicon`() {
        // Given
        val requestURI = URI.create("/favicon.ico")
        val httpMethod = HttpMethod.GET
        val httpStatus = HttpStatus.NO_CONTENT

        val request = createMockServerRequest(requestURI, httpMethod)

        // When
        val response = mainRouter.route(request).map { it.handle(request) }

        // Then
        assertThat(response)
            .isNotNull
            .isNotEmpty
            .get()
            .returns(httpStatus, from { it.statusCode() })
    }

    @Test
    fun `should handle user verification requesto`() {
        // Given
        val requestURI = URI.create("/api/users/verify")
        val httpMethod = HttpMethod.POST
        val httpStatus = HttpStatus.OK

        val request = createMockServerRequest(
            requestURI,
            httpMethod,
            """
            {
                "username": "test-username",
                "password": "test-password"
            }
            """.trimIndent()
        )

        every { userService.verifyUser(any()) } returns CurrentUser(id = -1L, authorities = listOf("ROLE_TEST"))

        // When
        val response = mainRouter.route(request).map { it.handle(request) }

        // Then
        assertThat(response)
            .isNotNull
            .isNotEmpty
            .get()
            .returns(httpStatus, from { it.statusCode() })
    }

    private fun createMockServerRequest(uri: URI, method: HttpMethod, content: String? = null): ServerRequest =
        ServerRequest.create(
            MockHttpServletRequest(method.name(), uri.toString())
                .also { it.setContent(content?.encodeToByteArray()) }
                .also { it.contentType = "application/json" },
            messageConverters,
        )
}
