package com.github.arhor.aws.graphql.federation.comments.api.listener;

import io.awspring.cloud.sqs.listener.interceptor.MessageInterceptor;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class IncomingEventInterceptor implements MessageInterceptor<Object> {

    @Nonnull
    @Override
    public Message<Object> intercept(@Nonnull final Message<Object> message) {
        return message;
    }

    @Override
    public void afterProcessing(@Nonnull final Message<Object> message, @Nullable final Throwable t) {
        MessageInterceptor.super.afterProcessing(message, t);
    }
}
