package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.config.ConfigureDatabase;
import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.test.ConfigureTestObjectMapper;
import com.github.arhor.aws.graphql.federation.config.ConfigureAdditionalBeans;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(
    classes = {
        ConfigureDatabase.class,
        ConfigureAdditionalBeans.class,
        ConfigureTestObjectMapper.class,
    }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:12");

    @Autowired
    private CommentRepository commentRepository;

    @DynamicPropertySource
    public static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    @Test
    void should_return_expected_list_of_comments_by_user_ids() {
        // given
        final var user1Comments = commentRepository.saveAll(
            List.of(
                new CommentEntity(1L, 1L, "user-1 / post-1 / comment-1"),
                new CommentEntity(1L, 1L, "user-1 / post-1 / comment-2")
            )
        );
        final var user2Comments = commentRepository.saveAll(
            List.of(
                new CommentEntity(2L, 1L, "user-2 / post-1 / comment-1"),
                new CommentEntity(2L, 1L, "user-2 / post-1 / comment-2")
            )
        );
        final var user3Comments = commentRepository.saveAll(
            List.of(
                new CommentEntity(3L, 1L, "user-3 / post-1 / comment-1"),
                new CommentEntity(3L, 1L, "user-3 / post-1 / comment-2")
            )
        );
        final var expectedComments = Stream.concat(user1Comments.stream(), user2Comments.stream()).toList();

        // when
        final var result = commentRepository.findAllByUserIdIn(
            expectedComments
                .stream()
                .map(CommentEntity::userId)
                .toList()
        );

        // then
        assertThat(result)
            .isNotNull()
            .containsExactlyElementsOf(expectedComments)
            .doesNotContainAnyElementsOf(user3Comments);
    }

    @Test
    void should_return_expected_list_of_comments_by_post_ids() {
        // given
        final var post1Comments = commentRepository.saveAll(
            List.of(
                new CommentEntity(1L, 1L, "user-1 / post-1 / comment-1"),
                new CommentEntity(1L, 1L, "user-1 / post-1 / comment-2")
            )
        );
        final var post2Comments = commentRepository.saveAll(
            List.of(
                new CommentEntity(1L, 2L, "user-1 / post-2 / comment-1"),
                new CommentEntity(1L, 2L, "user-1 / post-2 / comment-2")
            )
        );
        final var post3Comments = commentRepository.saveAll(
            List.of(
                new CommentEntity(1L, 3L, "user-1 / post-3 / comment-1"),
                new CommentEntity(1L, 3L, "user-1 / post-3 / comment-2")
            )
        );
        final var expectedComments = Stream.concat(post1Comments.stream(), post2Comments.stream()).toList();

        // when
        final var result = commentRepository.findAllByPostIdIn(
            expectedComments
                .stream()
                .map(CommentEntity::postId)
                .toList()
        );

        // then
        assertThat(result)
            .isNotNull()
            .containsExactlyElementsOf(expectedComments)
            .doesNotContainAnyElementsOf(post3Comments);
    }
}
