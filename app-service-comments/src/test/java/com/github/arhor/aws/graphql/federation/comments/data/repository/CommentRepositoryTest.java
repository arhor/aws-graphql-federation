package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.config.ConfigureDatabase;
import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.test.ConfigureTestObjectMapper;
import com.github.arhor.aws.graphql.federation.spring.core.ConfigureCoreApplicationComponents;
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
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJdbcTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(
    classes = {
        ConfigureCoreApplicationComponents.class,
        ConfigureDatabase.class,
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
                createComment(1L, 1L, "user-1 / post-1 / comment-1"),
                createComment(1L, 1L, "user-1 / post-1 / comment-2")
            )
        );
        final var user2Comments = commentRepository.saveAll(
            List.of(
                createComment(2L, 1L, "user-2 / post-1 / comment-1"),
                createComment(2L, 1L, "user-2 / post-1 / comment-2")
            )
        );
        final var user3Comments = commentRepository.saveAll(
            List.of(
                createComment(3L, 1L, "user-3 / post-1 / comment-1"),
                createComment(3L, 1L, "user-3 / post-1 / comment-2")
            )
        );
        final var expectedComments = Stream.concat(user1Comments.stream(), user2Comments.stream()).toList();

        // When
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
                createComment(1L, 1L, "user-1 / post-1 / comment-1"),
                createComment(1L, 1L, "user-1 / post-1 / comment-2")
            )
        );
        final var post2Comments = commentRepository.saveAll(
            List.of(
                createComment(1L, 2L, "user-1 / post-2 / comment-1"),
                createComment(1L, 2L, "user-1 / post-2 / comment-2")
            )
        );
        final var post3Comments = commentRepository.saveAll(
            List.of(
                createComment(1L, 3L, "user-1 / post-3 / comment-1"),
                createComment(1L, 3L, "user-1 / post-3 / comment-2")
            )
        );
        final var expectedComments = Stream.concat(post1Comments.stream(), post2Comments.stream()).toList();

        // When
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

    @Test
    void should_nullify_user_id_for_the_given_list_of_comments() {
        // given
        final var userId = 1L;
        final var postId = 1L;
        final var comments = commentRepository.saveAll(
            List.of(
                createComment(userId, postId, "user-" + userId + " / post-" + postId + " / comment-1"),
                createComment(userId, postId, "user-" + userId + " / post-" + postId + " / comment-2")
            )
        );

        // When
        commentRepository.unlinkAllFromUsers(Set.of(1L));

        final var commentsByUserId = commentRepository.findAllByUserIdIn(List.of(1L));
        final var commentsByPostId = commentRepository.findAllByPostIdIn(List.of(1L));

        // then
        assertSoftly(soft -> {
            soft.assertThat(commentsByUserId)
                .isNotNull()
                .isEmpty();

            soft.assertThat(commentsByPostId)
                .isNotNull()
                .hasSameSizeAs(comments)
                .allMatch(it -> it.userId() == null, CommentEntity.Fields.userId + " should be null");
        });
    }

    @Test
    void should_delete_all_comments_with_a_given_post_id() {
        // given
        final var userId = 1L;
        final var postId = 1L;
        final var comments = commentRepository.saveAll(
            List.of(
                createComment(userId, postId, "user-" + userId + " / post-" + postId + " / comment-1"),
                createComment(userId, postId, "user-" + userId + " / post-" + postId + " / comment-2")
            )
        );

        // When
        commentRepository.deleteAllFromPost(userId);

        final var commentsById = commentRepository.findAllById(comments.stream().map(CommentEntity::id).toList());
        final var commentsByUserId = commentRepository.findAllByUserIdIn(List.of(userId));
        final var commentsByPostId = commentRepository.findAllByPostIdIn(List.of(postId));

        // then
        assertSoftly(soft -> {
            soft.assertThat(commentsById)
                .isNotNull()
                .isEmpty();

            soft.assertThat(commentsByUserId)
                .isNotNull()
                .isEmpty();

            soft.assertThat(commentsByPostId)
                .isNotNull()
                .isEmpty();
        });
    }

    private CommentEntity createComment(final Long userId, final Long postId, final String content) {
        return CommentEntity.builder()
            .userId(userId)
            .postId(postId)
            .content(content)
            .build();
    }
}
