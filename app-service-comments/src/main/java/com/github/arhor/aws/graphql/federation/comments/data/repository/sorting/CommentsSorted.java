package com.github.arhor.aws.graphql.federation.comments.data.repository.sorting;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;

public final class CommentsSorted {

    private CommentsSorted() { /* Constants holder. Should not be instantiated */ }

    @NotNull
    public static Sort byCreatedDateTimeAsc() {
        return LazyHolder.BY_CREATED_DATE_TIME_ASC;
    }

    @NotNull
    public static Sort byCreatedDateTimeDesc() {
        return LazyHolder.BY_CREATED_DATE_TIME_DESC;
    }

    private static class LazyHolder {
        private static final Sort BY_CREATED_DATE_TIME_ASC = Sort.by(
            Sort.Direction.ASC,
            CommentEntity.Fields.createdDateTime
        );
        private static final Sort BY_CREATED_DATE_TIME_DESC = Sort.by(
            Sort.Direction.DESC,
            CommentEntity.Fields.createdDateTime
        );
    }
}
