package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.data.repository.CommentRepository;
import com.github.arhor.aws.graphql.federation.comments.service.mapper.CommentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@SpringJUnitConfig
class CommentServiceTest {

    @Configuration
    @ComponentScan(
        includeFilters = {@Filter(type = ASSIGNABLE_TYPE, classes = CommentService.class)},
        useDefaultFilters = false
    )
    static class Config {
    }

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    @Test
    void getCommentsByUserIds_should_not_interact_with_repository_if_userIds_is_empty() {
        // given
        var userIds = Collections.<Long>emptyList();

        // when
        var result = commentService.getCommentsByUserIds(userIds);

        // then
        verifyNoInteractions(commentRepository, commentMapper);

        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    void getCommentsByPostIds_should_not_interact_with_repository_if_postIds_is_empty() {
        // given
        var postIds = Collections.<Long>emptyList();

        // when
        var result = commentService.getCommentsByPostIds(postIds);

        // then
        verifyNoInteractions(commentRepository, commentMapper);

        assertThat(result)
            .isNotNull()
            .isEmpty();
    }
}
