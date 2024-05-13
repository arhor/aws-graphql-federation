package com.github.arhor.aws.graphql.federation.comments.api.listener;

import io.awspring.cloud.sqs.listener.interceptor.MessageInterceptor;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IncomingEventInterceptor implements MessageInterceptor<Object> {

    @Nonnull
    @Override
    public Message<Object> intercept(@Nonnull final Message<Object> message) {
        log.info(">>> Start processing SQS message: {}", message);
        return MessageInterceptor.super.intercept(message);
    }

    @Override
    public void afterProcessing(@Nonnull final Message<Object> message, @Nullable final Throwable t) {
        log.info("<<< Close processing SQS message: {}", message, t);
        MessageInterceptor.super.afterProcessing(message, t);
    }
}
