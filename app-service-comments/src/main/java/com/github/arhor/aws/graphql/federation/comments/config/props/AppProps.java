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
    Aws aws,

    @NotNull
    @Min(0)
    Integer retryAttempts
) {
    public record Aws(
        @Valid
        @NotNull
        Sqs sqs
    ) {
        public record Sqs(
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
