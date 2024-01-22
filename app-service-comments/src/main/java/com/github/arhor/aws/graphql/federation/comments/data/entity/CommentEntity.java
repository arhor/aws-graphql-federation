package com.github.arhor.aws.graphql.federation.comments.data.entity;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("comments")
@Immutable
public record CommentEntity(
    @Id
    @Column("id")
    @Nullable
    Long id,

    @Column("user_id")
    @Nullable
    Long userId,

    @Column("post_id")
    Long postId,

    @Column("content")
    String content,

    @Version
    @Column("version")
    @Nullable
    Long version,

    @CreatedDate
    @Column("created_date_time")
    @Nullable
    LocalDateTime createdDateTime,

    @LastModifiedDate
    @Column("updated_date_time")
    @Nullable
    LocalDateTime updatedDateTime
) {
    public CommentEntity(final Long userId, final Long postId, final String content) {
        this(null, userId, postId, content, null, null, null);
    }

    public CopyBuilder copy() {
        return new CopyBuilder(this);
    }

    public static class CopyBuilder {
        private Long id;
        private Long userId;
        private Long postId;
        private String content;
        private Long version;
        private LocalDateTime createdDateTime;
        private LocalDateTime updatedDateTime;

        public CopyBuilder(final CommentEntity that) {
            this.id = that.id;
            this.userId = that.userId;
            this.postId = that.postId;
            this.content = that.content;
            this.version = that.version;
            this.createdDateTime = that.createdDateTime;
            this.updatedDateTime = that.updatedDateTime;
        }

        public CommentEntity build() {
            return new CommentEntity(id, userId, postId, content, version, createdDateTime, updatedDateTime);
        }

        public CopyBuilder withId(final Long id) {
            this.id = id;
            return this;
        }

        public CopyBuilder withUserId(final Long userId) {
            this.userId = userId;
            return this;
        }

        public CopyBuilder withPostId(final Long postId) {
            this.postId = postId;
            return this;
        }

        public CopyBuilder withContent(final String content) {
            this.content = content;
            return this;
        }

        public CopyBuilder withVersion(final Long version) {
            this.version = version;
            return this;
        }

        public CopyBuilder withCreatedDateTime(final LocalDateTime createdDateTime) {
            this.createdDateTime = createdDateTime;
            return this;
        }

        public CopyBuilder withUpdatedDateTime(final LocalDateTime updatedDateTime) {
            this.updatedDateTime = updatedDateTime;
            return this;
        }
    }
}
