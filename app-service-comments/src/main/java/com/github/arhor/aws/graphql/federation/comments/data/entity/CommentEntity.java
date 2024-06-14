package com.github.arhor.aws.graphql.federation.comments.data.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("comments")
@Immutable
@Builder(toBuilder = true)
@FieldNameConstants
public record CommentEntity(
    @Id
    @Nullable
    @Column("id")
    UUID id,

    @Column("user_id")
    @Nullable
    UUID userId,

    @Nonnull
    @Column("post_id")
    UUID postId,

    @Nullable
    @Column("prnt_id")
    UUID prntId,

    @Nonnull
    @Column("content")
    String content,

    @Nonnull
    @Version
    @Column("version")
    Long version,

    @Nonnull
    @CreatedDate
    @Column("created_date_time")
    LocalDateTime createdDateTime,

    @Nullable
    @LastModifiedDate
    @Column("updated_date_time")
    LocalDateTime updatedDateTime
) {
}
