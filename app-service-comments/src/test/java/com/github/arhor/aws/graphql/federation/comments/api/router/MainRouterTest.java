package com.github.arhor.aws.graphql.federation.comments.api.router;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

class MainRouterTest {

    private final RouterFunction<ServerResponse> mainRouter =
        new ConfigureMainRouter().mainRouter();
    private final List<HttpMessageConverter<?>> messageConverters =
        List.of(new MappingJackson2HttpMessageConverter());

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void should_return_empty_response_with_NO_CONTENT_status_trying_to_get_favicon() throws Exception {
        // given
        var requestURI = URI.create("/favicon.ico");
        var httpMethod = HttpMethod.GET;
        var httpStatus = HttpStatus.NO_CONTENT;

        var request = createMockServerRequest(requestURI, httpMethod);

        // When
        var response =
            mainRouter.route(request)
                .get()
                .handle(request);

        // then
        assertThat(response)
            .isNotNull()
            .returns(httpStatus, from(ServerResponse::statusCode));
    }

    private ServerRequest createMockServerRequest(URI uri, HttpMethod method) {
        final var mockHttpServletRequest = new MockHttpServletRequest(method.name(), uri.toString());

        mockHttpServletRequest.setContentType("application/json");

        return ServerRequest.create(mockHttpServletRequest, messageConverters);
    }
}
