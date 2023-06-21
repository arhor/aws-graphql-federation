package com.github.arhor.dgs.articles.data.listener;

import com.github.arhor.dgs.articles.data.entity.ArticleEntity;
import org.springframework.data.relational.core.mapping.event.RelationalDeleteEvent;
import org.springframework.data.relational.core.mapping.event.RelationalSaveEvent;

/**
 * Interface represents User entity state change.
 */
public sealed interface ArticleStateChange {

    /**
     * Concrete type of change.
     */
    String type();

    record Updated(Long id) implements ArticleStateChange {

        public Updated(final RelationalSaveEvent<ArticleEntity> user) {
            this(user.getEntity().getId());
        }

        @Override
        public String type() {
            return "ArticleStateChange.Updated";
        }
    }

    record Deleted(Long id) implements ArticleStateChange {

        public Deleted(final RelationalDeleteEvent<ArticleEntity> event) {
            this((Long) event.getId().getValue());
        }

        @Override
        public String type() {
            return "ArticleStateChange.Deleted";
        }
    }
}
