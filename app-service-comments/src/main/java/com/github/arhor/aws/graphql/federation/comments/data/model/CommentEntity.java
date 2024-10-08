package com.github.arhor.aws.graphql.federation.comments.data.model;

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
    @Column("id")
    UUID id,

    @Column("user_id")
    UUID userId,

    @Column("post_id")
    UUID postId,

    @Column("prnt_id")
    UUID prntId,

    @Column("content")
    String content,

    @Version
    @Column("version")
    Long version,

    @CreatedDate
    @Column("created_date_time")
    LocalDateTime createdDateTime,

    @LastModifiedDate
    @Column("updated_date_time")
    LocalDateTime updatedDateTime
) {}
