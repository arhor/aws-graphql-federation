package com.github.arhor.dgs.articles.data.listener;

import com.github.arhor.dgs.articles.data.entity.ArticleEntity;
import io.awspring.cloud.sns.core.SnsNotification;
import io.awspring.cloud.sns.core.SnsOperations;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.MissingRequiredPropertiesException;
import org.springframework.data.relational.core.mapping.event.AbstractRelationalEventListener;
import org.springframework.data.relational.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.messaging.MessagingException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Retryable(retryFor = MessagingException.class)
public class ArticleStateChangeEventListener extends AbstractRelationalEventListener<ArticleEntity> {

    public static final String ARTICLE_UPDATED_EVENTS_PROP = "application-props.aws.sns.article-updated-events";
    public static final String ARTICLE_DELETED_EVENTS_PROP = "application-props.aws.sns.article-deleted-events";

    private static final String HEADER_PAYLOAD_TYPE = "xPayloadType";

    private final SnsOperations snsOperations;
    private final String articleUpdatedEventsTopic;
    private final String articleDeletedEventsTopic;

    @Autowired
    public ArticleStateChangeEventListener(
        final SnsOperations snsOperations,
        @Value("${" + ARTICLE_UPDATED_EVENTS_PROP + ":#{null}}") final String articleUpdatedEventsTopic,
        @Value("${" + ARTICLE_DELETED_EVENTS_PROP + ":#{null}}") final String articleDeletedEventsTopic
    ) {
        final var updatedDestMissing = articleUpdatedEventsTopic == null;
        final var deletedDestMissing = articleDeletedEventsTopic == null;

        if (updatedDestMissing || deletedDestMissing) {
            final var error = new MissingRequiredPropertiesException();
            final var props = error.getMissingRequiredProperties();

            if (updatedDestMissing) {
                props.add(ARTICLE_UPDATED_EVENTS_PROP);
            }
            if (deletedDestMissing) {
                props.add(ARTICLE_DELETED_EVENTS_PROP);
            }
            throw error;
        }
        this.snsOperations = snsOperations;
        this.articleUpdatedEventsTopic = articleUpdatedEventsTopic;
        this.articleDeletedEventsTopic = articleDeletedEventsTopic;
    }

    @Override
    public void onAfterSave(@Nonnull final AfterSaveEvent<ArticleEntity> event) {
        sendNotification(
            articleUpdatedEventsTopic,
            new ArticleStateChange.Updated(event)
        );
    }

    @Override
    public void onAfterDelete(@Nonnull final AfterDeleteEvent<ArticleEntity> event) {
        sendNotification(
            articleDeletedEventsTopic,
            new ArticleStateChange.Deleted(event)
        );
    }

    private void sendNotification(final String destination, final ArticleStateChange payload) {
        snsOperations.sendNotification(
            destination,
            new SnsNotification<>(
                payload,
                Map.of(HEADER_PAYLOAD_TYPE, payload.type())
            )
        );
    }
}
