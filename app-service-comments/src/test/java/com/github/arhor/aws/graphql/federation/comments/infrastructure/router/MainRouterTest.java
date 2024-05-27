package com.github.arhor.aws.graphql.federation.comments.infrastructure.router;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ConfigureMainRouter.class)
class MainRouterTest {

    @Autowired
    private MockMvc http;

    @Test
    void should_return_empty_response_with_NO_CONTENT_status_trying_to_get_favicon() throws Exception {
        // Given
        final var requestURI = URI.create("/favicon.ico");

        // When
        final var result = http.perform(get(requestURI));

        // Then
        result
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }
}
