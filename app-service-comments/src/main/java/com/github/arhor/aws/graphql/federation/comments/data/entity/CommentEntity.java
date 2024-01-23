package com.github.arhor.aws.graphql.federation.comments.data.entity;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
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
@Builder(toBuilder = true)
@FieldNameConstants(asEnum = true)
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
}
