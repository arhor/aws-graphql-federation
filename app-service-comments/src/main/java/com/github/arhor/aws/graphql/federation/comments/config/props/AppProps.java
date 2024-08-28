package com.github.arhor.aws.graphql.federation.comments.config.props;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app-props")
public record AppProps(
    @Valid
    @NotNull
    Events events,

    @NotNull
    @Min(0)
    Integer retryAttempts
) {
    public record Events(
        @Valid
        @NotNull
        Source source
    ) {
        public record Source(
            @NotBlank
            String syncCommentsOnPostCreatedEvent,

            @NotBlank
            String syncCommentsOnPostDeletedEvent,

            @NotBlank
            String syncCommentsOnUserCreatedEvent,

            @NotBlank
            String syncCommentsOnUserDeletedEvent
        ) {}
    }
}
