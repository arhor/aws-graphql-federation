package com.github.arhor.aws.graphql.federation.comments.data.entity;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Immutable
@Builder(toBuilder = true)
@FieldNameConstants(asEnum = true)
public record UserEntity(
    @Id
    @Column("id")
    @Nullable
    Long id
) {
}
