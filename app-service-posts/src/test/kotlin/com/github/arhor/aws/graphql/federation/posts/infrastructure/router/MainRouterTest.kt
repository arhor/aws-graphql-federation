package com.github.arhor.aws.graphql.federation.posts.infrastructure.router

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.net.URI

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = [MainRouter::class])
class MainRouterTest {

    @Autowired
    private lateinit var http: MockMvc

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
}
