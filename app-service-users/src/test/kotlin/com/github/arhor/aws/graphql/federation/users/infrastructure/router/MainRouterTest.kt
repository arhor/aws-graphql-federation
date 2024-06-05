package com.github.arhor.aws.graphql.federation.users.infrastructure.router

import com.github.arhor.aws.graphql.federation.starter.security.CurrentUser
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.users.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.net.URI
import java.util.UUID

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = [MainRouter::class])
class MainRouterTest {

    @Autowired
    private lateinit var http: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    @Test
    fun `should return empty response with NO_CONTENT status trying to get favicon`() {
        // Given
        val requestURI = URI.create("/favicon.ico")

        // When
        val result = http.get(uri = requestURI)

        // Then
        result.andExpect {
            status { isNoContent() }
            content { string("") }
        }
    }

    @Test
    fun `should handle user verification request`() {
        // Given
        val requestURI = URI.create("/api/users/verify")

        val expectedReq = CurrentUserRequest(username = "test-username", password = "test-password")
        val expectedRes = CurrentUser(id = UUID.randomUUID(), authorities = listOf("ROLE_TEST_1", "ROLE_TEST_2"))
        val expectedContentType = MediaType.APPLICATION_JSON

        every { userService.getUserByUsernameAndPassword(any()) } returns expectedRes

        // When
        val result = http.post(uri = requestURI) {
            content = """
                {
                    "username": "${expectedReq.username}",
                    "password": "${expectedReq.password}"
                }
            """.trimIndent()
            contentType = expectedContentType
        }

        // Then
        result.andExpect {
            status { isOk() }

            content { contentType(expectedContentType) }

            jsonPath("$.id") { value(expectedRes.id.toString()) }
            jsonPath("$.authorities") { value(contains(*expectedRes.authorities.toTypedArray())) }
        }

        verify(exactly = 1) { userService.getUserByUsernameAndPassword(expectedReq) }
    }
}
